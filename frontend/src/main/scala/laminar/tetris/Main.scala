package laminar.tetris

import org.scalajs.dom.document
import zio.{BootstrapRuntime, CancelableFuture, UIO, URIO, ZEnv, ZIO}
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

  val program: URIO[zio.ZEnv, Unit] = (for {
    container <- UIO.effectTotal(document.getElementById("root"))
    _ <- GameRendering.renderInContainer(container, "Random", Grid.defaultWidth, Grid.defaultHeight)
  } yield ()).provideLayer(RandomGameEngine.layer(Grid.defaultWidth, Grid.defaultHeight))

}


object ZioSyntax {
  implicit final class ZioOps[A](val z: ZIO[ZEnv, Throwable, A]) extends AnyVal {
    def runAsync: CancelableFuture[A] = zio.Runtime.default.unsafeRunToFuture(z)
  }
}