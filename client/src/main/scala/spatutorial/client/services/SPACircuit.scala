package spatutorial.client.services

import autowire._
import diode._
import diode.data._
import diode.util._
import diode.react.ReactConnector
import spatutorial.shared.Api
import spatutorial.model.Company
import boopickle.Default._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

// Actions
case class UpdateMotd(potResult: Pot[String] = Empty) extends PotAction[String, UpdateMotd] {
  override def next(value: Pot[String]) = UpdateMotd(value)
}

case object RefreshCompanies extends Action
case class UpdateAllCompanies(companies: Seq[Company]) extends Action

// The base model of our application
case class RootModel(motd: Pot[String], companies: Pot[Seq[Company]])

class CompaniesHandler[M](modelRW: ModelRW[M, Pot[Seq[Company]]]) extends ActionHandler(modelRW) {
  override def handle = {
    case RefreshCompanies =>
    effectOnly(Effect(AjaxClient[Api].getAllCompanies().call().map(UpdateAllCompanies)))
    case UpdateAllCompanies(companies) =>
    updated(Ready(companies))
  }
}

/**
  * Handles actions related to the Motd
  *
  * @param modelRW Reader/Writer to access the model
  */
class MotdHandler[M](modelRW: ModelRW[M, Pot[String]]) extends ActionHandler(modelRW) {
  implicit val runner = new RunAfterJS

  override def handle = {
    case action: UpdateMotd =>
      val updateF = action.effect(AjaxClient[Api].welcomeMsg("User X").call())(identity _)
      action.handleWith(this, updateF)(PotAction.handler())
  }
}

// Application circuit
object SPACircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  // initial application model
  override protected def initialModel = RootModel(Empty, Empty)
  // combine all handlers into one
  override protected val actionHandler = composeHandlers(
    new MotdHandler(zoomRW(_.motd)((m, v) => m.copy(motd = v))),
    new CompaniesHandler(zoomRW(_.companies)((m, v) => m.copy(companies = v)))
  )
}
