import sbt.Keys._
import sbt.Project.projectToRef

scalaVersion in ThisBuild := Settings.versions.scala
ensimeIgnoreSourcesInBase in ThisBuild := true

lazy val slickCodegenDeps = Seq(
  "com.h2database" % "h2" % Settings.versions.h2,
  "com.typesafe.slick" %% "slick-codegen" % Settings.versions.slick,
  "com.typesafe.slick" %% "slick" % Settings.versions.slick
)

/** codegen project containing the customized code generator */
lazy val codegen = project
    .settings(
      scalaVersion := Settings.versions.scala,
      scalacOptions ++= Settings.scalacOptions,
      libraryDependencies ++= slickCodegenDeps
    )

def hasDbCodegen(codegenTask : Def.Initialize[Task[Seq[File]]]): Project => Project =
  _.settings(
    libraryDependencies ++= slickCodegenDeps,
    slick <<= codegenTask, // register manual sbt command
    sourceGenerators in Compile <+= codegenTask // register automatic code generation on every compile, remove for only manual use
)
.dependsOn(codegen)

// code generation task that calls the customized code generator
lazy val slick = taskKey[Seq[File]]("gen-tables")
def slickDbCodegenTask(codegen : String, pkg : String) = Def.task {
  val dir = sourceManaged.value
  val cp = (dependencyClasspath in Compile).value
  val r = (runner in Compile).value
  val s = streams.value
  val outputDir = (dir / "slick").getPath // place generated files in sbt's managed sources folder
  val url = "jdbc:h2:mem:test;INIT=runscript from 'project/create.sql'" // connection info for a pre-populated throw-away, in-memory db for this demo, which is freshly initialized on every run
  val jdbcDriver = "org.h2.Driver"
  val slickDriver = "slick.driver.H2Driver"

  toError(r.run("spatutorial.codegen.Codegen", cp.files, Array(codegen, slickDriver, jdbcDriver, url, outputDir, pkg), s.log))
  val fname = outputDir + "/" + pkg.replace(".", "/") + "/Tables.scala"
  Seq(file(fname))
}

// a special crossProject for configuring a JS/JVM/shared structure
lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .configureAll(hasDbCodegen(slickDbCodegenTask("shared", "spatutorial.model")))
  .settings(
    scalaVersion := Settings.versions.scala,
    libraryDependencies ++= Settings.sharedDependencies.value
  )
  // set up settings specific to the JS project
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJVM = shared.jvm.settings(name := "sharedJVM")

lazy val sharedJS = shared.js.settings(name := "sharedJS")

// use eliding to drop some debug code in the production build
lazy val elideOptions = settingKey[Seq[String]]("Set limit for elidable functions")

// instantiate the JS project for SBT with some additional settings
lazy val client: Project = (project in file("client"))
  .settings(
    name := "client",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.scalajsDependencies.value,
    npmDevDependencies in Compile += "expose-loader" -> "0.7.1",
    npmDependencies in Compile ++= Settings.npmDependencies.value,
    // by default we do development build, no eliding
    elideOptions := Seq(),
    scalacOptions ++= elideOptions.value,
    // RuntimeDOM is needed for tests
    jsDependencies += RuntimeDOM % "test",
    persistLauncher := false,
    // use uTest framework for tests
    testFrameworks += new TestFramework("utest.runner.Framework"),
    webpackConfigFile := Some(baseDirectory.value / "spa.webpack.config.js")
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb, ScalaJSBundlerPlugin)
  .dependsOn(sharedJS)

// Client projects (just one in this case)
lazy val clients = Seq(client)

// instantiate the JVM project for SBT with some additional settings
lazy val server = (project in file("server"))
  .configure(hasDbCodegen(slickDbCodegenTask("server", "spatutorial.model")))
  .settings(
    name := "server",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.jvmDependencies.value,
    commands += ReleaseCmd,
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,
    // connect to the client project
    scalaJSProjects := clients,
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    npmAssets ++= Settings.npmAssets(client).value,
    // compress CSS
    LessKeys.compress in Assets := true
  )
  .enablePlugins(PlayScala, WebScalaJSBundlerPlugin)
  .disablePlugins(PlayLayoutPlugin) // use the standard directory layout instead of Play's custom
  .aggregate(clients.map(projectToRef): _*)
  .dependsOn(sharedJVM)

// Command for building a release
lazy val ReleaseCmd = Command.command("release") {
  state => "set elideOptions in client := Seq(\"-Xelide-below\", \"WARNING\")" ::
    "client/clean" ::
    "client/test" ::
    "server/clean" ::
    "server/test" ::
    "server/dist" ::
    "set elideOptions in client := Seq()" ::
    state
}

// loads the Play server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
