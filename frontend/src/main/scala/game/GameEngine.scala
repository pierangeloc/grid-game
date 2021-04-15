package game

import eu.timepit.refined.numeric.NonNegative
import eu.timepit.refined.{refineMV, refineV}
import game.GameEngine.Score
import GameEngine.{Score, State}
import Grid.{ColorMap, GridPosition}
import zio.clock.Clock
import zio.{Has, UIO, ZIO}
import zio.random.Random
import zio.stream.{UStream, ZStream}

trait GameEngine {
  def stateStream: UStream[State]
  def scoreStream: UStream[Score]
}

object GameEngine {
  case class State(w: NonNeg, h: NonNeg, private val grid: ColorMap = Map()) { self =>
    def cell(gp: GridPosition): Option[Color] = grid.get(gp)
    def update(gp: GridPosition, c: Color): State = {
      copy(grid = self.grid.updated(gp, c))
    }
  }

  object State {
    val zero: NonNeg = refineMV(0)
    def uniform(w: NonNeg, h: NonNeg, c: Color) = new State(w, h,
      (for {
        row <- (0 until h.value)
        col <- (0 until w.value)
        } yield GridPosition(refineV[NonNegative](row).getOrElse(zero), refineV[NonNegative](col).getOrElse(zero)) -> c).toMap
    )
  }

  case class Score(points: Int)

  def stateStream: ZStream[Has[GameEngine], Nothing, State] =
    ZStream.accessStream[Has[GameEngine]](_.get.stateStream)

  def scoreStream: ZStream[Has[GameEngine], Nothing, Score] =
    ZStream.accessStream[Has[GameEngine]](_.get.scoreStream)
}

object RandomGameEngine {
  import zio.duration._

  def layer(w: NonNeg, h: NonNeg) = (
    for {
      env <- ZIO.environment[Random with Clock]
    } yield new GameEngine {

      private val randomColorNoBg: ZIO[Random, Nothing, Color] =
        zio.random.nextInt.map(Color.fromIndex).flatMap { c =>
           if (c != Color.Background) UIO(c) else randomColorNoBg
        }

      private val randomPos: ZIO[Random, Nothing, GridPosition] =
        zio.random.nextIntBounded(w.value).zipWith(zio.random.nextIntBounded(h.value)) {
          case (c, r) => GridPosition(unsafeNonNeg(c), unsafeNonNeg(r))
        }

      override def stateStream: UStream[State] =
        ZStream.unfoldM[Random with Clock, Nothing, State, State](State.uniform(Grid.defaultWidth, Grid.defaultHeight, Color.Background)) { s =>
          (randomColorNoBg zipWith randomPos) {
            case (c, p) =>
              val newState = s.update(p, c)
              Some(newState -> newState)
          }
        }.fixed(5.millis).provide(env)

      override def scoreStream: UStream[Score] = ZStream.repeatEffect(
        zio.random.nextIntBounded(100000)
      ).fixed(5.seconds).map(Score.apply).provide(env)
    }
  ).toLayer
}
