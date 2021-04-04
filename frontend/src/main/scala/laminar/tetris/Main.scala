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
  val _ = css
  @JSExportTopLevel("main")
  def main(): Unit = {
    val container = document.getElementById("root")
    val sizeVar = Var("")

    val nameVar = Var("")
    def TextInput(): Input = input(typ := "text")

    val squareSize = 10

    div(
      "Please enter your name: ",
      TextInput().amend(onInput.map(_.toString) --> nameVar)
    )

    case class Rgb(r: Int, g: Int, b: Int)
    def color(): String = {
      val r = scala.util.Random.nextInt(256)
      val g = scala.util.Random.nextInt(256)
      val b = scala.util.Random.nextInt(256)
      s"rgba($r,$g,$b,0.5)"
    }


    val rVar = Var(0)
    val gVar = Var(0)
    val bVar = Var(0)

    render(container,
      div(
        label("Hello"),
        label("How are you?"),
        input(
          typ := "text",
          className := "SizeInput",
          placeholder := "10"
        ).amend(onInput.map(_.toString) --> sizeVar),
        p(
          child.text <-- sizeVar.signal
        ),
        div(
          (1 to squareSize).map{ _ =>
            div(
              width := "10px",
              height := "10px",
              background := color()
            )
          }
        ),
        input(
          typ := "text",
          className := "SizeInput",
          placeholder := "10"
        ).amend {
          onInput.mapToValue.map(_.toInt) --> rVar
        },
        input(
          typ := "text",
          className := "SizeInput",
          placeholder := "10"
        ).amend {
          onInput.mapToValue.map(_.toInt) --> gVar
        },
        input(
          typ := "text",
          className := "SizeInput",
          placeholder := "10"
        ).amend {
          onInput.mapToValue.map(_.toInt) --> bVar
        },
        div(
          width := "100px",
          height := "100px",
          background <-- rVar.signal.combineWith(gVar.signal.combineWith(bVar.signal)).map {
            case (r, g, b) => s"rgba($r,$g,$b,1)"
          }
        )



      )
    )
    ()
  }
}
