import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import scalajsbundler.sbtplugin.NpmAssets

/**
 * Application settings. Configure the build for your application here.
 * You normally don't have to touch the actual build definition after this.
 */
object Settings {
  /** The name of your application */
  val name = "scalajs-spa"

  /** The version of your application */
  val version = "1.1.4"

  /** Options for the scala compiler */
  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scala = "2.11.8"
    val scalaDom = "0.9.1"
    val scalajsReact = "0.11.3"
    val scalaCSS = "0.5.1"
    val log4js = "1.4.15"
    val autowire = "0.2.5"
    val booPickle = "1.2.5"
    val diode = "1.1.0"
    val uTest = "0.4.4"

    val react = "15.6.1"
    val chartjs = "2.1.3"
    val fontAwesome = "4.7.0"
    val semanticUi = "2.2.13"
    val scalajsReactComponents = "0.6.0"
    val semanticUiReact = "0.71.4"

    val scalajsScripts = "1.0.0"

    val h2 = "1.4.193"
    val slick = "3.1.1"
    val logbackClassic = "1.2.2"
  }

  /**
   * These dependencies are shared between JS and JVM projects
   * the special %%% function selects the correct version for each project
   */
  val sharedDependencies = Def.setting(Seq(
    "com.lihaoyi" %%% "autowire" % versions.autowire,
    "me.chrons" %%% "boopickle" % versions.booPickle
  ))

  /** Dependencies only used by the JVM project */
  val jvmDependencies = Def.setting(Seq(
    "com.vmunier" %% "scalajs-scripts" % versions.scalajsScripts,
    "com.lihaoyi" %% "utest" % versions.uTest % Test
  ))

  /** Dependencies only used by the JS project (note the use of %%% instead of %%) */
  val scalajsDependencies = Def.setting(Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,
    "com.github.japgolly.scalacss" %%% "ext-react" % versions.scalaCSS,
    "me.chrons" %%% "diode" % versions.diode,
    "me.chrons" %%% "diode-react" % versions.diode,
    "org.scala-js" %%% "scalajs-dom" % versions.scalaDom,
    "com.lihaoyi" %%% "utest" % versions.uTest % Test,
    "com.olvind" %%% "scalajs-react-components" % versions.scalajsReactComponents
  ))

  /** Dependencies for external JS libs that are bundled into a single .js file according to dependency order */
  val npmDependencies = Def.setting(Seq(
      "react" -> versions.react,
      "react-dom" -> versions.react,
      "log4javascript" -> versions.log4js,
      "chart.js" -> versions.chartjs,
      "font-awesome" -> versions.fontAwesome,
      "semantic-ui" -> versions.semanticUi,
      "semantic-ui-react" -> versions.semanticUiReact
      ))

  def npmAssets(project: ProjectReference) = {
    NpmAssets.ofProject(project) {
      nodeModules =>
        (nodeModules / "font-awesome/css").*** +++
        (nodeModules / "font-awesome/fonts").*** +++
        (nodeModules / "semantic-ui/dist").***
    }
  }
}
