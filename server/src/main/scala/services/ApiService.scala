package services

import java.util.{UUID, Date}

import spatutorial.shared._
import spatutorial.model._
import scala.concurrent.Await
import scala.concurrent.duration._
import slick.lifted.TableQuery
import Tables.profile.api._

class ApiService extends Api {

  val url = "jdbc:h2:mem:test;INIT=runscript from 'project/create.sql'"
  val db = Database.forURL(url, driver = "org.h2.Driver")

  override def welcomeMsg(name: String): String =
    s"Welcome to SPA, $name! Time is now ${new Date}"

  def getAllCompanies(): Seq[Company] = {
     Await.result(db.run {
       Tables.Companies.result
     }, 20.seconds)
  }

}
