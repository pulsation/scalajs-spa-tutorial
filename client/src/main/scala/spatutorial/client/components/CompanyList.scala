package spatutorial.client.components

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.components.Bootstrap._
import spatutorial.client.services.UpdateAllCompanies
import spatutorial.model.Company

/**
* This is a simple component demonstrating how to display async data coming from the server
*/
object CompanyList {

  // create the React component for holding the Message of the Day
  val CompanyList = ReactComponentB[ModelProxy[Pot[Seq[Company]]]]("Companies")
  .render_P { proxy =>

    <.div(
      proxy().renderPending(_ > 500, _ => <.p("Loading...")),
      proxy().renderFailed(ex => <.p("Failed to load")),
      proxy().render(companies =>
        <.table(
          companies.map(company =>
            <.tr(
              <.td(company.name)
            )
          )
        )
      )
    )
  }
  .build


def apply(proxy: ModelProxy[Pot[Seq[Company]]]) = CompanyList(proxy)
}
