package util

import org.openrndr.math.Vector2
import org.openrndr.shape.Shape

class ColorIndexedShape(val s: Shape, val cI: Int, val vel: Vector2 = Vector2.ZERO) {
  var shape: Shape = s
  var colorIndex = cI
  var velocity = vel
}
