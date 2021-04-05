package laminar.tetris

import scala.scalajs.js.annotation.JSExportTopLevel
import com.raquo.laminar.api.L._
import org.scalajs.dom.document

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("resources/index.css", JSImport.Default)
@js.native
object Css extends js.Object

object Main {

  private val css = Css
  val _ = css //to make compiler warnings disappear

  @JSExportTopLevel("main")
  def main(): Unit = {
    val container = document.getElementById("root")

    case class PixelLength(n: Int) {
      def toPx = s"${n}px"
    }

    def gameCell(size: PixelLength) = div(
      width := size.toPx,
      height := size.toPx,
      cls := ("bg-green-500", "rounded-md", "border-2")
    )

    def gameRow(size: PixelLength, nrCells: Int) = div(
      cls := ("flex", "flex-row"),
      List.fill(nrCells)(gameCell(size))
    )

    def gameSquare(width: Int, height: Int, cellSize: PixelLength) = div(
      cls := ("flex", "flex-col"),
      List.fill(height)(gameRow(cellSize, width))
    )

    render(container,
      div(
        cls := ("flex", "flex-col", "border-8", "font-mono"),
        div(
          cls := ("content-center", "p-20"),
          p("Tetris", cls := ("text-8xl", "border-15"))
        ),
        gameSquare(50, 20, PixelLength(25)),
        div(
          cls := ("flex", "flex-row", "border-8", "py-11"),
          p("Score", cls := ("text-base", "border-4")),
          p("50", cls := ("border-4"))
        )
      )
    )
    ()
  }
}
