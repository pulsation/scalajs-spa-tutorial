package spatutorial.client.modules

import diode.data.Pot
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.SPAMain.{Loc, TodoLoc}
import spatutorial.client.components.CompanyList
import spatutorial.shared.Api
import spatutorial.model.Company

import scala.util.Random
import scala.language.existentials

object Companies {

  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[Pot[Seq[Company]]])

  case class State(companiesRowWrapper: ReactConnectProxy[Pot[Seq[Company]]])

  private val component = ReactComponentB[Props]("Companies")
    // create and store the connect proxy in state for later use
    .initialState_P(props => State(props.proxy.connect(m => m)))
    .renderPS { (_, props, state) =>
      <.div(
        <.h2("Companies"),
        <.div(state.companiesRowWrapper(CompanyList(_)))
      )
    }
    .build

  def apply(router: RouterCtl[Loc], proxy: ModelProxy[Pot[Seq[Company]]]) = component(Props(router, proxy))
}
