package game

import game.GameEngine.State
import game.Tetris.Tetromino
import game.Tetris.Tetromino.{Position, Rotation}
import zio.clock.Clock
import zio.stream.{UStream, ZStream}
import zio.{Has, ZIO, ZLayer}

object Tetris {

  //TIL the Tetris pieces are called https://tetris.fandom.com/wiki/Tetromino
  sealed trait Tetromino
  object Tetromino {
    case object I extends Tetromino
    case object J extends Tetromino
    case object L extends Tetromino
    case object O extends Tetromino
    case object S extends Tetromino
    case object T extends Tetromino
    case object Z extends Tetromino

    type Matrix = List[List[Int]]
    //The position of a Tetromino in the grid. Top left is (0, 0), growing to the bottom right
    case class Position(x: NonNeg, y: NonNeg)
    sealed trait Rotation
    object Rotation {
      case object North extends Rotation
      case object East extends Rotation
      case object South extends Rotation
      case object West extends Rotation

      def next(rotation: Rotation): Rotation = rotation match {
        case North => East
        case East  => South
        case South => West
        case West  => North
      }
    }

    /**
     * Matrix representation of every piece. This makes the rotation algorithm very simple
     */
    def matrixRepr(t: Tetromino): Matrix = t match {
      case I => List(List(1, 1, 1, 1))
      case J => List(List(1, 0, 0),
                     List(1, 1, 1))
      case L => List(List(0, 0, 1),
                     List(1, 1, 1))
      case O => List(List(1, 1),
                     List(1, 1))
      case S => List(List(0, 1, 1),
                     List(1, 1, 0))
      case T => List(List(1, 1, 1),
                     List(0, 1, 0))
      case Z => List(List(1, 1, 0),
                     List(0, 1, 1))
    }

    /**
     * see https://vikkrraant.medium.com/scala-style-matrix-rotation-and-spirals-post-5-46211a77ebe6
     */
    def rotate(m: Matrix): Matrix = m.transpose.map(_.reverse)
    def rotate(original: Matrix, rotation: Rotation): Matrix = rotation match {
      case Rotation.North => original
      case Rotation.East  => rotate(original)
      case Rotation.South => rotate(rotate(original))
      case Rotation.West  => rotate(rotate(rotate(original)))
    }

    def matrixWithPositions(m: Matrix): List[((Int, Int), Int)] = m.map(_.zipWithIndex).zipWithIndex.flatMap {
      case (row, rowIx) => row.map {
        case (col, colIx) => ((rowIx, colIx), col)
      }
    }

    def positions(m: Matrix, origin: Position): List[Position] = matrixWithPositions(m).collect {
      case ((x, y), 1) => Position(unsafeNonNeg(origin.x.value + x), unsafeNonNeg(origin.y.value + y))
    }

    def color(t: Tetromino): Color = t match {
      case I => Color.Cyan
      case J => Color.Blue
      case L => Color.Orange
      case O => Color.Yellow
      case S => Color.Green
      case T => Color.Purple
      case Z => Color.Red
    }

    def blocksToGridState(blocks: List[(Position, Tetromino, Rotation)], w: NonNeg, h: NonNeg): State = {
      blocks.map {
        case (pos, block, rotation) =>
          val col = color(block)
          val matrix = matrixRepr(block)
          val rotated = rotate(matrix, rotation)
          val poss = positions(rotated, pos)
          (col, poss)
      }.foldLeft(State.uniform(w, h, Color.Background)) {
        case (state, (color, positions)) =>
          positions.foldLeft(state)((s, pos) => s.update(pos.y, pos.x, color))
      }
    }

  }
}

object TetrisGameEngine {
  import zio.duration._
  val layer: ZLayer[Clock, Nothing,  Has[GameEngine]] = {

    val blocks = List(
      (Position(unsafeNonNeg(0), unsafeNonNeg(4)), Tetromino.I, Rotation.North),
      (Position(unsafeNonNeg(5), unsafeNonNeg(6)), Tetromino.O, Rotation.East),
      (Position(unsafeNonNeg(10), unsafeNonNeg(6)), Tetromino.L, Rotation.South),
      (Position(unsafeNonNeg(15), unsafeNonNeg(6)), Tetromino.S, Rotation.West),
      (Position(unsafeNonNeg(20), unsafeNonNeg(6)), Tetromino.T, Rotation.East),
    )

    (for {
      env <- ZIO.environment[Clock]
    } yield new GameEngine {
      override def stateStream: UStream[GameEngine.State] =
        ZStream.repeat(Tetromino.blocksToGridState(blocks, Grid.defaultWidth, Grid.defaultHeight))
          .fixed(1.seconds).provide(env)

      override def scoreStream: UStream[GameEngine.Score] = ???
    }).toLayer

  }
}
