package spatutorial.shared

import spatutorial.model._

trait Api {
  // message of the day
  def welcomeMsg(name: String): String

  def getAllCompanies(): Seq[Company]
}
