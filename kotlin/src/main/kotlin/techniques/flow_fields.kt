package techniques

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.perlinLinear
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Rectangle

abstract class BaseFlowField(width: Int, height: Int) {
  val width = width
  val height = height

  abstract fun getAngle_degrees(vec: Vector2): Double

  open fun drawField(
    drawer: Drawer,
    num_x_steps: Int = 50,
    num_y_steps: Int = 50,
    arrow_width_scale: Double = 0.7,
    arrow_height_scale: Double = 0.2
  ) {

    val rect_width = arrow_width_scale * width / num_x_steps
    val rect_height = arrow_height_scale * width / num_x_steps
    val circle_radius = rect_height

    for (x in 0..width step width / num_x_steps) {
      for (y in 0..height step height / num_y_steps) {
        val point = Vector2(x.toDouble(), y.toDouble())
        val corner = Vector2(-rect_width / 2, -rect_height / 2)
        val angle = getAngle_degrees(point)
        val arrow = Rectangle(corner, rect_width, rect_height)
        val circle = Circle(corner.x + circle_radius / 2, 0.0, circle_radius)

        drawer.isolated {
          drawer.fill = ColorRGBa.BLACK.opacify(.1)
          drawer.stroke = ColorRGBa.TRANSPARENT

          drawer.translate(point)
          drawer.rotate(angle)
          drawer.rectangle(arrow)
          drawer.circle(circle)
        }
      }
    }
  }
}


class PerlinLinear_FlowField(seed: Int, width: Int, height: Int, scaler: Double = 5.0) : BaseFlowField(width, height) {
  var seed = seed
  var scaler = scaler

  override fun getAngle_degrees(vec: Vector2): Double {
    // Scale the input vector between 0 and 1
    val scaled_vec = Vector2(vec.x / width, vec.y / height)
    val noise = perlinLinear(seed, scaled_vec) * scaler

    return noise * 360.0
  }
}

class DoublePerlinLinear_FlowField(seed: Int, width: Int, height: Int, scaler: Double = 5.0) : BaseFlowField(width, height) {
  var seed = seed
  var scaler = scaler

  override fun getAngle_degrees(vec: Vector2): Double {
    // Scale the input vector between 0 and 1
    val scaled_vec = Vector2(vec.x / width, vec.y / height)
    val seed_2 : Long = (seed * 1L * seed) % Int.MAX_VALUE
    val noise = perlinLinear(seed, scaled_vec) * perlinLinear(seed_2.toInt(),scaled_vec) * scaler

    return noise * 360.0
  }
}

class FlowField_F00(width: Int, height: Int) : BaseFlowField(width, height) {
  var scaler_a = Random.gaussian(-1.0, 2.0);
  var scaler_b = Random.double(-1.0, 1.0);
  var x_offst = Random.double(-1.0, 1.0);
  var y_offst = Random.double(-1.0, 1.0);

  override fun getAngle_degrees(vec: Vector2): Double {
    var x_scaled = vec.x / width
    var y_scaled = vec.y / height

    var angle =
      360 * (scaler_a * (x_scaled + x_offst) * (y_scaled + y_offst) + (y_scaled) * scaler_b)
    return angle
  }
}
