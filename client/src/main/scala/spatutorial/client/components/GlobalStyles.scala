package spatutorial.client.components

import scalacss.Defaults._

object GlobalStyles extends StyleSheet.Inline {
  import dsl._

  style(unsafeRoot("body")(
    paddingLeft(70.px),
    paddingRight(70.px)
  ))
}
