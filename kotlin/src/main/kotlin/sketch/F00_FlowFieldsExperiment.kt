package sketch

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.math.Vector2
import org.openrndr.shape.*
import org.openrndr.extensions.Screenshots
import org.openrndr.PresentationMode
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.gaussian
import org.openrndr.shape.Rectangle
import org.openrndr.extra.noise.random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.round
import kotlin.math.PI

fun roundToNearestN(x:Double, N:Double) :Double{
  return round(x / N) * N
}
fun main() = application {
  var field_random_scaler_a = 1.0;
  var field_random_scaler_b = 1.0;
  var field_random_x_offst = 1.0;
  var field_random_y_offst = 1.0;

  configure {
    width = 1200 // Width of picture
    height = 1200 // Height of picture
  }

  fun calculateAngle(vec: Vector2, width: Double, height: Double): Double {
    var x_scaled = vec.x / width
    var y_scaled = vec.y / height

    var angle = 360 * (field_random_scaler_a*(x_scaled + field_random_x_offst) * (y_scaled + field_random_y_offst) + (y_scaled) * field_random_scaler_b) / 2

    return angle
  }

  program {
    // Setup the picture for presentation mode which will go to the next
    // iteration on button press
    window.presentationMode = PresentationMode.MANUAL
    mouse.buttonUp.listen {
      window.requestDraw()
    }

    val colors = listOf(
      ColorRGBa.fromHex("FCB264"), // orange
      ColorRGBa.fromHex("8DC0E2"), // blue
      ColorRGBa.fromHex("C4B2E1"), // purple
      ColorRGBa.fromHex("CEE4F2"), // light blue
      ColorRGBa.fromHex("FEE1C3"), // light orange
      ColorRGBa.fromHex("E9E2F4"), // light purple
    )

    // One time setup
    var num_x_steps = 50
    var num_y_steps = 50
    var arrow_width_scale = .7
    var arrow_height_scale = .2

    var rect_width = arrow_width_scale * width/num_x_steps
    var rect_height = arrow_height_scale * width/num_x_steps
    var circle_radius = rect_height
    var draw_field: Boolean = false
    // Take a timestamped screenshot with the space bar
    extend(Screenshots())
    extend {
//      Random.rnd = kotlin.random.Random(123211242112321)
      field_random_scaler_a = Random.gaussian(-1.0, 1.0);
      field_random_scaler_b = Random.double(-1.0, 1.0);
      field_random_x_offst = Random.double(-1.0, 1.0);
      field_random_y_offst = Random.double(-1.0, 1.0);

      // Draw here
      drawer.clear(ColorRGBa.WHITE)

      if (draw_field) {
        for (x in 0..width step width / num_x_steps) {
          for (y in 0..height step height / num_y_steps) {
            var point = Vector2(x.toDouble(), y.toDouble())
            var corner = Vector2(-rect_width / 2, -rect_height / 2)
            var angle = calculateAngle(point, width.toDouble(), height.toDouble())
            var arrow = Rectangle(corner, rect_width, rect_height)
            var circle = Circle(corner.x + circle_radius / 2, 0.0, circle_radius)

            drawer.isolated {
              drawer.fill = ColorRGBa.BLACK.alphaMultiplied(.1)
              drawer.stroke = ColorRGBa.BLACK.alphaMultiplied(.1)
              drawer.translate(point)
              drawer.rotate(angle)
              drawer.rectangle(arrow)
              drawer.circle(circle)
            }
          }
        }
      }

      // To make a lot of thin lines go with a increased number of lines, a low thickness and  small lengths
      // the 10 spacing with a radius of 50% is pretty perfect, but other effects can be achieved
      var num_lines = 1000
      var thickness = 1// 3 dots
      var spacing = 10.0
      var circle_fill_percent = 0.5
      var max_length_percent = 0.6
      var min_length_percent = 0.1
      var radius = spacing * circle_fill_percent

      for (i in 0..num_lines) {
        var color = colors.random()
        var length = random(min_length_percent*width.toDouble(), width.toDouble()*max_length_percent) //gaussian(width.toDouble()*.8, 100.0).toInt()
        var start_position = Vector2(roundToNearestN(random(-width*.5, width*1.5), spacing), roundToNearestN(random(-height*.5, height*1.5), spacing))
        var point = start_position
        var polarity = arrayOf(-1.0, 1.0).random()


        for (i in 0..length.toInt()) {
          var angle = calculateAngle(point, width.toDouble(), height.toDouble())

          if (i % spacing.toInt() == 0) {
            // This makes a cool angular effect
//                        point = Vector2(
//              roundToNearestN(point.x, 2.0),
//              roundToNearestN(point.y, 2.0),
//              )
            drawer.isolated {
              drawer.fill = color
              drawer.stroke = null
              drawer.translate(point)
              drawer.rotate(angle)

              for (i in 1..thickness) {
                var circle_center = (thickness.toDouble()) * .5
                drawer.circle(Circle(0.0, spacing*(i - circle_center - 1) , radius))
              }
            }
          }
          point = Vector2(point.x + polarity * cos(angle*PI/180.0), point.y + polarity* sin(angle* PI/180))

        }
      }
    }
  }
}
