package util

import org.openrndr.math.Vector2
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.contour

fun makeRhombus(startingPosition: Vector2, width: Double, height: Double, dx: Double = 0.0, dy: Double = 0.0): ShapeContour {
  return contour {
    moveTo(startingPosition)
    lineTo(startingPosition.x + width, startingPosition.y + dy)
    lineTo(startingPosition.x + width + dx, startingPosition.y + height + dy)
    lineTo(startingPosition.x + dx, startingPosition.y + height)
    close()
  }
}
