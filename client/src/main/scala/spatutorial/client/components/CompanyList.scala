package spatutorial.client.components

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.components.Bootstrap._
import spatutorial.client.services.UpdateAllCompanies
import spatutorial.model.Company
import chandu0101.scalajs.react.components.{ReactTable, ReactMouseEventB}
import chandu0101.scalajs.react.components.ReactTable.Model
import chandu0101.scalajs.react.components.semanticui.SuiButton
import spatutorial.client.services.RefreshCompanies

object CompanyList {

  val CompanyList = ReactComponentB[ModelProxy[Pot[Seq[Company]]]]("Companies")
  .render_P { proxy =>

    <.div(
      proxy().renderPending(_ > 500, _ => <.p("Loading...")),
      proxy().renderFailed(ex => <.p("Failed to load")),
      proxy().render(companies => {
        val companiesData = companies.toVector
          .map(company => Map("name" -> company.name, "id" -> company.id))
        ReactTable(data = companiesData, columns = List("id", "name"), rowsPerPage = 10)
      }
    ),
    SuiButton(primary = true, onClick = { mouseEvent:ReactMouseEventB => { proxy.dispatchCB(RefreshCompanies) }})("Update"))
  }
  .build

def apply(proxy: ModelProxy[Pot[Seq[Company]]]) = CompanyList(proxy)
}
