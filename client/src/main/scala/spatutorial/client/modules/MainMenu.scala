package spatutorial.client.modules

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.SPAMain.{DashboardLoc, Loc, CompaniesLoc}
import spatutorial.client.components.Icon._
import spatutorial.client.components._
import spatutorial.client.services._

import scalacss.ScalaCssReact._

object MainMenu {
  // shorthand for styles

  case class Props(router: RouterCtl[Loc], currentLoc: Loc)

  private case class MenuItem(idx: Int, label: (Props) => ReactNode, icon: Icon, location: Loc)

  private val menuItems = Seq(
    MenuItem(1, _ => "Dashboard", Icon.dashboard, DashboardLoc),
    MenuItem(3, _ => "Companies", Icon.table, CompaniesLoc)
  )

  private class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      <.div(^.className := "ui borderless main menu", ^.marginBottom := "1em")(
        // build a list of menu items
        for (item <- menuItems) yield {
          <.a(^.key := item.idx, ^.className := "item", (props.currentLoc == item.location) ?= (^.className := "active"),
            props.router.link(item.location)(item.icon, " ", item.label(props))
          )
        }
      )
    }
  }

  private val component = ReactComponentB[Props]("MainMenu")
    .renderBackend[Backend]
    .build

  def apply(ctl: RouterCtl[Loc], currentLoc: Loc): ReactElement =
    component(Props(ctl, currentLoc))
}
