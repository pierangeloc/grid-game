package game

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

    /**
     * Position occupied by a Tetronimo without rotation, The origin (0, 0) is the leftmost block.
     * If there are 2 blocks on the left, the bottom left is the origin.
     */
    def relativePos(t: Tetromino): List[(Int, Int)] = t match {
      case I => List((0, 0), (1, 0), (2, 0), (3, 0))
      case J => List((0, 0), (0, 1), (1, 0), (2, 0))
      case L => List((0, 0), (1, 0), (2, 0), (2, 1))
      case O => List((0, 0), (0, 1), (1, 0), (1, 1))
      case S => List((0, 0), (1, 0), (1, 1), (2, 1))
      case T => List((0, 0), (1, 0), (2, 0), (1, 1))
      case Z => List((0, 0), (1, 0), (1, -1), (2, -1))
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

  }
}
