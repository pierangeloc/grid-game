package game

import zio.{UIO, URIO}
import zio.stream._
import com.raquo.laminar.api.L._
import game.GameEngine.Score
import game.Cell.{colorToCls, defaultSize}
import game.GameEngine.{Score, State}

sealed trait Color
object Color {
  case object Red extends Color
  case object Green extends Color
  case object Blue extends Color
  case object Yellow extends Color
  case object Background extends Color
  case object Orange extends Color
  case object Cyan extends Color
  case object Purple extends Color

  val all = List(Background, Red, Green, Blue, Yellow, Orange, Cyan, Purple)

  def fromIndex(i: Int): Color = all(i.abs.toInt % all.size)
}

case class PixelLength(n: Int) {
  def toPx = s"${n}px"
}

case class Cell(row: Int, column: Int, color: Color)

object Cell {
  val defaultSize: PixelLength = PixelLength(25)

  def colorToCls(color: Color): List[String] = color match {
    case Color.Background => List("bg-gray-800")
    case Color.Red        => List("bg-red-500")
    case Color.Green      => List("bg-green-500")
    case Color.Blue       => List("bg-blue-500")
    case Color.Yellow     => List("bg-yellow-200")
    case Color.Cyan       => List("bg-blue-200")
    case Color.Orange     => List("bg-yellow-600")
    case Color.Purple     => List("bg-purple-500")
  }
}

object Grid {
  import eu.timepit.refined._
  import eu.timepit.refined.numeric._
  import eu.timepit.refined.auto._

  //The position of a cell in the grid. Top left is (0, 0), growing to the bottom right
  case class GridPosition(x: NonNeg, y: NonNeg)
  object GridPosition {
    val origin = GridPosition(zero, zero)
  }

  type ColorMap = Map[GridPosition, Color]

  val defaultWidth: NonNeg   = refineMV[NonNegative](20)
  val defaultHeight: NonNeg  = refineMV[NonNegative](15)

  def fromStateStream[R](width: NonNeg, height: NonNeg, states: ZStream[R, Nothing, State]): URIO[R, Div] = for {
    varState <- UIO.effectTotal(Var[State](State.uniform(width, height, Color.Background)))
    div  <- UIO.effectTotal(
      div(
        cls := ("flex", "flex-col"),
        (0 until height.value).map { r =>
          gameRow(defaultSize, width, unsafeNonNeg(r), varState)
        }
      )
    )
    _ <- states.foreach(state => UIO.effectTotal(varState.set(state))).forkDaemon
  } yield div

  private def gameRow(size: PixelLength, width: NonNeg, rowNr: NonNeg, state: Var[State]) = div(
    cls := ("flex", "flex-row"),
    (0 until width.value).map { c =>
      val colNr = unsafeNonNeg(c)
      gameCell(size, state.signal.map(_.cell(GridPosition(colNr, rowNr)).getOrElse(Color.Background)))
    }
  )

  private def gameCell(size: PixelLength, colorSignal: Signal[Color]) = div(
    width := size.toPx,
    height := size.toPx,
    cls := ("rounded-md", "border-2"),
    cls <-- colorSignal.map(colorToCls)
  )

}

object GameScore {

  def fromScoreStream[R](scores: ZStream[R, Nothing, Score]): URIO[R, Div] = for {
    varState <- UIO.effectTotal(Var[Score](Score(0)))
    _ <- UIO.effectTotal(println("VAR DEFINED"))
    div  <- UIO.effectTotal(
      div(
        cls := ("flex", "flex-row", "border-8", "py-11"),
        p("Score", cls := ("text-base", "border-4")),
        p(
          cls := "border-4",
          child.text <-- varState.signal.map(_.points.toString)
        )
      )
    )
    _ <- scores.foreach(state => UIO.effectTotal(varState.set(state))).forkDaemon
  } yield div

}