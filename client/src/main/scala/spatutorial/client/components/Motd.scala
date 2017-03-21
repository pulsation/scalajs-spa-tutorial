package spatutorial.client.components

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.services.UpdateMotd
import chandu0101.scalajs.react.components.semanticui.{SuiButton,SuiSegment}
import chandu0101.scalajs.react.components.ReactMouseEventB

/**
  * This is a simple component demonstrating how to display async data coming from the server
  */
object Motd {

  // create the React component for holding the Message of the Day
  val Motd = ReactComponentB[ModelProxy[Pot[String]]]("Motd")
    .render_P { proxy =>
      SuiSegment()(
        // render messages depending on the state of the Pot
        proxy().renderPending(_ > 500, _ => <.p("Loading...")),
        proxy().renderFailed(ex => <.p("Failed to load")),
        proxy().render(m => <.p(m)),
        SuiButton(onClick = { mouseEvent:ReactMouseEventB => proxy.dispatchCB(UpdateMotd()) })("Update")
      )
    }
    .componentDidMount(scope =>
      // update only if Motd is empty
      Callback.when(scope.props.value.isEmpty)(scope.props.dispatchCB(UpdateMotd()))
    )
    .build

  def apply(proxy: ModelProxy[Pot[String]]) = Motd(proxy)
}
