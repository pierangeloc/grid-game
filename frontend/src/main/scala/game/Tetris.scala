package game

import game.GameEngine.State
import game.Grid.GridPosition
import game.Tetris.Tetromino.{Position, color}
import game.Tetris.Tetromino
import zio.clock.Clock
import zio.stream.{UStream, ZStream}
import zio.{Has, ZIO, ZLayer}


/**
 * We model the positions according to a cartesian reference system centered on the top left corner of the screen,
 * with the x axis pointing to the right, and the y axis pointing upwards
 */
object Tetris {

  case class Tetromino(position: Position, kind: Tetromino.Kind, relativePos: List[Position]) {
    def rotate90: Tetromino = {
      copy(
        relativePos = relativePos
          .map { case Position(x, y) => Position(-y, x) }
      )
    }

    def moveRight = copy(position = position.right)
    def moveLeft  = copy(position = position.left)
    def moveUp    = copy(position = position.up)
    def moveDown  = copy(position = position.down)

    //this way we can calculate positions on the grid even of blocks (partially) out of the grid
    def occupiedGrid: List[GridPosition] = relativePos.map(rp => Position(rp.x + position.x, rp.y + position.y)).collect {
      case Position(x, y) if x > 0 && y < 0 => GridPosition(unsafeNonNeg(x), unsafeNonNeg(-y))
    }
  }

  object Tetromino {

    //TIL the Tetris pieces are called https://tetris.fandom.com/wiki/Tetromino
    sealed trait Kind

    object Kind {
      case object I extends Kind

      case object J extends Kind

      case object L extends Kind

      case object O extends Kind

      case object S extends Kind

      case object T extends Kind

      case object Z extends Kind
    }

    object Canonical {
      val I = Tetromino(position = Position.origin, Kind.I, List(Position(0, -1), Position(0, 0), Position(0, 1), Position(0, 2)))
      val J = Tetromino(position = Position.origin, Kind.J, List(Position(-1, -1), Position(0, -1), Position(0, 0), Position(0, 1)))
      val L = Tetromino(position = Position.origin, Kind.L, List(Position(-1, 1), Position(0, -1), Position(0, 0), Position(0, 1)))
      val O = Tetromino(position = Position.origin, Kind.O, List(Position(-1, -1), Position(0, -1), Position(0, 0), Position(-1, 0)))
      val S = Tetromino(position = Position.origin, Kind.S, List(Position(-1, -1), Position(0, -1), Position(0, 0), Position(1, 0)))
      val T = Tetromino(position = Position.origin, Kind.T, List(Position(-1, 0), Position(0, 0), Position(1, 0), Position(0, -1)))
      val Z = Tetromino(position = Position.origin, Kind.Z, List(Position(-1, 0), Position(0, 0), Position(0, -1), Position(1, -1)))
    }

    case class Position(x: Int, y: Int) {
      def right = copy(x = x + 1)
      def left = copy(x = x - 1)
      def up = copy(y = y + 1)
      def down = copy(y = y - 1)
    }

    object Position {
      val origin = Position(0, 0)
    }

    def color(t: Kind): Color = t match {
      case Kind.I => Color.Cyan
      case Kind.J => Color.Blue
      case Kind.L => Color.Orange
      case Kind.O => Color.Yellow
      case Kind.S => Color.Green
      case Kind.T => Color.Purple
      case Kind.Z => Color.Red
    }
  }

  def blocksToGridState(blocks: List[Tetromino], w: NonNeg, h: NonNeg): State = {
    blocks.flatMap { t =>
        val col = color(t.kind)
        t.occupiedGrid.map(pos => pos -> col)
    }.foldLeft(State.uniform(w, h, Color.Background)) {
      case (state, (pos, color)) =>
        state.update(pos, color)
    }
  }

}

object TetrisGameEngine {
  import zio.duration._
  val layer: ZLayer[Clock, Nothing,  Has[GameEngine]] = {

    val blocks: List[Tetromino] = List(
      Tetromino.Canonical.I.copy(position = Position(3, -6)),
      Tetromino.Canonical.O.copy(position = Position(3, -2)),
      Tetromino.Canonical.L.copy(position = Position(8, -2)),
      Tetromino.Canonical.S.copy(position = Position(15, -2)),
      Tetromino.Canonical.T.copy(position = Position(10, -6))
    )

    (for {
      env <- ZIO.environment[Clock]
    } yield new GameEngine {
      override def stateStream: UStream[GameEngine.State] = {
        ZStream.unfold(blocks) { blocks =>
          val newBlocks: List[Tetromino] = blocks.map(_.rotate90)
          Some(newBlocks -> newBlocks)
        }.map(blocks => Tetris.blocksToGridState(blocks, Grid.defaultWidth, Grid.defaultHeight))
        .fixed(1.seconds).provide(env)
      }

      override def scoreStream: UStream[GameEngine.Score] = ???
    }).toLayer

  }
}
