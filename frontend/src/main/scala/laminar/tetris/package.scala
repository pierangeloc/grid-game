package laminar

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.NonNegative
import eu.timepit.refined.{refineMV, refineV}

package object tetris {
  type NonNeg = Int Refined NonNegative

  def unsafeNonNeg(x: Int): NonNeg = refineV[NonNegative](x).getOrElse(refineMV(0))

}
