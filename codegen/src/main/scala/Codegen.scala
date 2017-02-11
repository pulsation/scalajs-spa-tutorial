package spatutorial.codegen

import slick.codegen.SourceCodeGenerator
import slick.{ model => m }
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import slick.driver.JdbcProfile
import scala.concurrent.duration.Duration

trait CustomizedGeneratorNames extends SourceCodeGenerator {
  override def entityName =
    dbTableName => dbTableName match {
        case "COMPANIES" => "Company"
        case _ => dbTableName.dropRight(1).toLowerCase.toCamelCase
    }

    override def tableName =
      dbTableName => dbTableName.toLowerCase.toCamelCase
}

class ServerCodegen(model: m.Model) extends SourceCodeGenerator(model)
with CustomizedGeneratorNames {
    override def Table = (t: m.Table) => new TableDef(t) {
      override def EntityType = new EntityType {
        override def doc = ""
        override def code = ""
      }
  }
}

class SharedCodegen(model: m.Model) extends SourceCodeGenerator(model)
with CustomizedGeneratorNames {
  override def packageCode(profile: String, pkg: String, container: String, parentType: Option[String]): String = {
    s"""package ${pkg}
          |
          |$code
          """.stripMargin
  }
  override def code = tables.map(_.code.mkString("\n")).mkString("\n\n")
  override def Table = (t: m.Table) => new Table(t) {
    override def PlainSqlMapper = new PlainSqlMapper {
      override def doc = ""
      override def code = ""
    }
    override def TableClass = new TableClass {
      override def doc = ""
      override def code = ""
    }
    override def TableValue = new TableValue {
      override def doc = ""
      override def code = ""
    }
  }
}

/** A runnable class to execute the code generator */
object Codegen {

  def run(whichCodegen: String, slickDriver: String, jdbcDriver: String, url: String, outputDir: String, pkg: String, user: Option[String], password: Option[String]): Unit = {
    val driver: JdbcProfile =
      Class.forName(slickDriver + "$").getField("MODULE$").get(null).asInstanceOf[JdbcProfile]
    val dbFactory = driver.api.Database
    val db = dbFactory.forURL(url, driver = jdbcDriver,
      user = user.getOrElse(null), password = password.getOrElse(null), keepAliveConnection = true)
    try {
      val m = Await.result(db.run(driver.createModel(None, false)(ExecutionContext.global).withPinnedSession), Duration.Inf)
      whichCodegen match {
        case "server" => new ServerCodegen(m).writeToFile(slickDriver,outputDir,pkg)
        case "shared" => new SharedCodegen(m).writeToFile(slickDriver,outputDir,pkg)
        case _ => {
          println(s"Codegen $whichCodegen could not be found.")
        }
      }
    } finally db.close
  }

  def main(args: Array[String]): Unit = {
    args.toList match {
      case whichCodegen :: slickDriver :: jdbcDriver :: url :: outputDir :: pkg :: Nil =>
          run(whichCodegen, slickDriver, jdbcDriver, url, outputDir, pkg, None, None)
      case _ => {
        println("""
            |Usage:
            |  SourceCodeGenerator slickDriver jdbcDriver url outputDir pkg [user password]
            |
            |Options:
            |  slickDriver: Fully qualified name of Slick driver class, e.g. "slick.driver.H2Driver"
            |  jdbcDriver: Fully qualified name of jdbc driver class, e.g. "org.h2.Driver"
            |  url: JDBC URL, e.g. "jdbc:postgresql://localhost/test"
            |  outputDir: Place where the package folder structure should be put
            |  pkg: Scala package the generated code should be places in
            |  user: database connection user name
            |  password: database connection password
          """.stripMargin.trim)
        System.exit(1)
      }
    }
  }

}
