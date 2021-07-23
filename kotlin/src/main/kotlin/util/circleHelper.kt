package util

import org.openrndr.extra.noise.Random
import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle

/**
 * Choose a random point on a circle around some angle.
 *
 * @param circle  Circle to constrain selection to
 * @param start_angle  Which angle on the circle to start with
 * @param max_delta  What angle away will we allow
 */
fun randomPointOnCircle(
  circle: Circle,
  start_angle: Double = 0.0,
  max_delta: Double = 180.0
): Pair<Vector2, Double> {
  val angle = Random.double(start_angle - max_delta, start_angle + max_delta)
  return Pair(circle.center + Vector2.fromPolar(Polar(angle, circle.radius)), angle)
}

