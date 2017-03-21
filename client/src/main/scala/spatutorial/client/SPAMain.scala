package spatutorial.client

import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import spatutorial.client.components.GlobalStyles
import spatutorial.client.logger._
import spatutorial.client.modules._
import spatutorial.client.services.{SPACircuit,RefreshCompanies}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._
import scalacss.internal.mutable.GlobalRegistry
import chandu0101.scalajs.react.components.{ReactTable,Pager}

@JSExport("SPAMain")
object SPAMain extends js.JSApp {

  // Define the locations (pages) used in this application
  sealed trait Loc

  case object DashboardLoc extends Loc

  case object CompaniesLoc extends Loc

  // configure the router
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._
    // wrap/connect components to the circuit
    (staticRoute(root, DashboardLoc) ~> renderR(ctl => SPACircuit.wrap(_.motd)(proxy => Dashboard(ctl, proxy)))
      | staticRoute("#companies", CompaniesLoc) ~> renderR(ctl => SPACircuit.wrap(_.companies)(proxy => Companies(ctl, proxy)))
      ).notFound(redirectToPage(DashboardLoc)(Redirect.Replace))
  }.renderWith(layout)

  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(
        <.div(^.className := "ui text container",
            MainMenu(c, r.page)
        ),
        // currently active module is shown in this container
        <.div(^.className := "container", r.render())
      )
  }

  @JSExport
  def main(): Unit = {
    log.warn("Application starting")
    // send log messages also to the server
    log.enableServerLogging("/logging")
    log.info("This message goes to server as well")

    GlobalRegistry.register(ReactTable.DefaultStyle, Pager.DefaultStyle)
    GlobalRegistry.addToDocumentOnRegistration()

    // create stylesheet
    GlobalStyles.addToDocument()

    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    ReactDOM.render(router(), dom.document.getElementById("root"))

    // Send initial action to circuit
    SPACircuit(RefreshCompanies)
  }
}
