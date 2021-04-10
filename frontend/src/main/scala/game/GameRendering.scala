package game

import com.raquo.laminar.api.L._
import org.scalajs.dom.raw.Element
import zio.{Has, UIO, URIO}

object GameRendering {

  def renderInContainer(container: Element, title: String, gridWidth: NonNeg, gridHeight: NonNeg): URIO[Has[GameEngine], Unit] = {
    val stateStream = GameEngine.stateStream
    val scoreStream = GameEngine.scoreStream
    for {
      gameGrid <- Grid.fromStateStream(gridWidth, gridHeight, stateStream)
      scoreDiv <- GameScore.fromScoreStream(scoreStream)
      _ <- UIO.effectTotal(render(container,
        div(
          cls := ("flex", "flex-col", "border-8", "font-mono"),
          div(
            cls := ("content-center", "p-20"),
            p(title, cls := ("text-8xl", "border-15"))
          ),
          gameGrid,
          scoreDiv
        )
      ))
    } yield ()
  }
}
