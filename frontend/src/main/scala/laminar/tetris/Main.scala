package laminar.tetris

import com.raquo.laminar.api.L._
import org.scalajs.dom.document
import zio.clock.Clock
import zio.{BootstrapRuntime, CancelableFuture, UIO, URIO, ZEnv, ZIO}
import zio.random.Random
import zio.stream.ZStream
import zio.duration._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}

@JSImport("resources/index.css", JSImport.Default)
@js.native
object Css extends js.Object

object Main extends BootstrapRuntime {
  println("Here we are")

  private val css = Css
  val _ = css //to make compiler warnings disappear

  @JSExportTopLevel("main")
  def main(): Unit = {
    unsafeRunAsync(program)(_ => ())

  }
//
//
//
//
//
//    Cell.fromColors(changingDiv).runAsync.onComplete {
//      case Success(d) =>
//        render(container,
//          div(
//            cls := ("flex", "flex-col", "border-8", "font-mono"),
//            div(
//              cls := ("content-center", "p-20"),
//              p("Tetris", cls := ("text-8xl", "border-15"))
//            ),
//            gameSquare(50, 20, PixelLength(25)),
//            div(
//              cls := ("flex", "flex-row", "border-8", "py-11"),
//              p("Score", cls := ("text-base", "border-4")),
//              p("50", cls := ("border-4"))
//            ),
//            d
//          )
//        )
//      case Failure(e) => println(s"Error + ${e}")
//    }
//
//
//    ()
//  }

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

  val changingDiv: ZStream[Random with Clock, Nothing, Color] = ZStream.repeatEffect(zio.random.nextIntBounded(Color.all.size).collect(new Exception(""))(
    i => Color.fromIndex(i) match {
      case Some(c) => c
    }
  ).orDie
  ).fixed(1.seconds)


  import zio.console

  val program2 = console.putStrLn("stmt1") *> console.putStrLn(
    "stmt2"
  )

  val program: URIO[zio.ZEnv, Unit] = (for {
    _ <- console.putStrLn("Starting the program")
    _ <- changingDiv.take(10).foreach(c => console.putStrLn(s"c: ${c}"))
    d <- Cell.fromColors(changingDiv)
    _ <- console.putStrLn("Created the div")
    container <- UIO.effectTotal(document.getElementById("root"))
    _ <- console.putStrLn(s"Got the container $container")

    _ <- UIO.effectTotal(render(container,
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
          ),
          d
        )
      ))
  } yield ()).orDie

}


object ZioSyntax {
  implicit final class ZioOps[A](val z: ZIO[ZEnv, Throwable, A]) extends AnyVal {
    def runAsync: CancelableFuture[A] = zio.Runtime.default.unsafeRunToFuture(z)
  }
}