package laminar.tetris

import zio.{RIO, UIO}
import zio.stream._
import com.raquo.laminar.api.L._
import eu.timepit.refined.api.Refined
import laminar.tetris.Cell.{cellSize, colorToCls}
import zio.console.Console

sealed trait Color
object Color {
  case object Red extends Color
  case object Green extends Color
  case object Blue extends Color
  case object Yellow extends Color
  case object Background extends Color

  val all = List(Background, Red, Green, Blue, Yellow)

  def fromIndex(i: Int): Color = all(i % all.size)
}

case class PixelLength(n: Int) {
  def toPx = s"${n}px"
}

case class Cell(row: Int, column: Int, color: Color)

object Cell {

  import zio.console
  val cellSize = PixelLength(25)

  def colorToCls(color: Color): List[String] = color match {
    case Color.Background => List("bg-gray-800")
    case Color.Red => List("bg-red-500")
    case Color.Green => List("bg-green-500")
    case Color.Blue => List("bg-blue-500")
    case Color.Yellow => List("bg-yellow-500")
  }

  def fromColors[R](colors: ZStream[R, Nothing, Color]): RIO[R with Console, Div] = for {
    varColor <- UIO.effectTotal(Var[Color](Color.Background))
    _ <- console.putStrLn("Starting...")
    div  <- UIO.effectTotal(
      div(
        width := cellSize.toPx,
        height := cellSize.toPx,
        cls := ("rounded-md", "border-2"),
        cls <-- varColor.signal.map(colorToCls)
      )
    )
    _ <- console.putStrLn("Built the div")
    _ <- colors.foreach(c => console.putStrLn(s"Color: ${c}") *> UIO.effectTotal(varColor.set(c))).forkDaemon
  } yield div
}

object Grid {
  import eu.timepit.refined._
  import eu.timepit.refined.numeric._
  import eu.timepit.refined.auto._

  type NonNeg = Int Refined NonNegative
  type ColorMap = Map[(NonNeg, NonNeg), Color]
  case class State(w: NonNeg, h: NonNeg, private val grid: ColorMap = Map()) { self =>
    def cell(row: NonNeg, col: NonNeg): Option[Color] = grid.get((row, col))
    def update(row: NonNeg, col: NonNeg, c: Color): State = {
      copy(grid = self.grid.updated((row, col), c))
    }
  }

  val defaultWidth: NonNeg   = refineMV[NonNegative](50)
  val defaultHeight: NonNeg  = refineMV[NonNegative](20)

  def unsafeNonNeg(x: Int): NonNeg = refineV[NonNegative](x).getOrElse(refineMV(0))

  object State {
    val zero: NonNeg = refineMV(0)
    def uniform(w: NonNeg, h: NonNeg, c: Color) = new State(w, h,
      (
        for {
          r <- (0 until h.value)
          c <- (0 until w.value)
        } yield (refineV[NonNegative](r).getOrElse(zero), refineV[NonNegative](c).getOrElse(zero))
      ).map(k => (k, c)).toMap
    )
  }

  def fromStates[R](width: NonNeg, height: NonNeg, states: ZStream[R, Nothing, State]): RIO[R with Console, Div] = for {
    varState <- UIO.effectTotal(Var[State](State.uniform(width, height, Color.Background)))
    div  <- UIO.effectTotal(
      div(
        cls := ("flex", "flex-col"),
        (0 until height.value).map { r =>
          gameRow(cellSize, width, unsafeNonNeg(r), varState)
        }
      )
    )
    _ <- states.foreach(state => UIO.effectTotal(varState.set(state))).forkDaemon
  } yield div

  def gameRow(size: PixelLength, width: NonNeg, rowNr: NonNeg, state: Var[State]) = div(
    cls := ("flex", "flex-row"),
    (0 until width.value).map { c =>
      val colNr = unsafeNonNeg(c)
      gameCell(size, state.signal.map(_.cell(rowNr, colNr).getOrElse(Color.Background)))
    }
  )

  def gameCell(size: PixelLength, colorSignal: Signal[Color]) = div(
    width := size.toPx,
    height := size.toPx,
    cls := ("rounded-md", "border-2"),
    cls <-- colorSignal.map(colorToCls)
  )

}

case class Grid(width: Int, height: Int) {

}