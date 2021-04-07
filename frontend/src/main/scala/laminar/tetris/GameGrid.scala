package laminar.tetris

import zio.{RIO, UIO}
import zio.stream._
import com.raquo.laminar.api.L._
import zio.console.Console

sealed trait Color
object Color {
  case object Background extends Color
  case object Red extends Color
  case object Green extends Color
  case object Blue extends Color
  case object Yellow extends Color

  val all = List(Background, Red, Green, Blue, Yellow)

  def fromIndex(i: Int): Option[Color] = i match {
    case 0 => Some(Background)
    case 1 => Some(Red)
    case 2 => Some(Green)
    case 3 => Some(Blue)
    case 4 => Some(Yellow)
    case _ => None
  }
}

case class PixelLength(n: Int) {
  def toPx = s"${n}px"
}

case class Cell(row: Int, column: Int, color: Color)

object Cell {

  import zio.console
  val cellSize = PixelLength(50)

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