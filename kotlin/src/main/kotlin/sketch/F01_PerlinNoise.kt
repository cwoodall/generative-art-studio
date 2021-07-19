package sketch

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extensions.Screenshots
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
//import org.openrndr.extra.noclear.NoClear
import org.openrndr.PresentationMode
import org.openrndr.extra.noise.*
import org.openrndr.shape.Rectangle
import palettes.Palette_00
import util.randomVector2
import util.roundToNearestN
import util.weightedRandomBool
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

import techniques.PerlinLinear_FlowField

fun main() = application {
  configure {
    width = 1200 // Width of picture
    height = 1200 // Height of picture
  }

  program {
    // Setup the picture for presentation mode which will go to the next
    // iteration on button press
    window.presentationMode = PresentationMode.MANUAL
    mouse.buttonUp.listen {
      window.requestDraw()
    }
    // Configuration values
    val seed = 123
    Random.rnd = kotlin.random.Random(seed)
    val colors = Palette_00()

    // One time setup
    // Take a timestamped screenshot with the space bar
    extend(Screenshots())
//    extend(NoClear())
    extend {
      // Draw here
      val field = PerlinLinear_FlowField(Random.int(), width, height)
      drawer.clear(ColorRGBa.BLACK)

      val draw_field = false

      if (draw_field) {
        field.drawField(drawer)
      }

      var margin_probability = .2
      var margin: Boolean = weightedRandomBool(margin_probability)
      var snap_probability = .6
      var snap: Boolean = weightedRandomBool(snap_probability)
      var density = random(0.001, 1.0)
      var chunkyness = random(0.1, 1.0)

      // To make a lot of thin lines go with a increased number of lines, a low thickness and  small lengths
      // the 10 spacing with a radius of 50% is pretty perfect, but other effects can be achieved
      var num_lines = 2000
      var thickness = 1
      var spacing = 4.0
      var circle_fill_percent = 0.5
      var max_length_percent = 0.8
      var min_length_percent = 0.1
      var radius = spacing * circle_fill_percent
      var margin_percent = if (margin) {
        .1
      } else {
        -.5
      }
      for (line_i in 0..num_lines) {
        var color = colors.random()


        // Generate the initial point
        var point: Vector2 = roundToNearestN(
          randomVector2(
            width * (margin_percent),
            width * (1 - margin_percent),
            height * (margin_percent),
            height * (1 - margin_percent)
          ), spacing
        )

        var length = random(
          min_length_percent * width.toDouble(),
          width.toDouble() * max_length_percent
        )

        var polarity = arrayOf(-1.0, 1.0).random()


        // Go through N integer spots across the image
        for (j in 0..length.toInt()) {
          var angle = field.getAngle_degrees(point)

          if (j % spacing.toInt() == 0) {
            // This makes a cool angular effect when snapping when we actually go to draw
            if (snap) {
              point = roundToNearestN(point, spacing)
            }

            drawer.isolated {
              drawer.fill = color
              drawer.stroke = null
              drawer.translate(point)
              drawer.rotate(angle)

              for (internal_dot in 1..thickness) {
                var circle_center = (thickness.toDouble()) * .5
                drawer.circle(Circle(0.0, spacing * (internal_dot - circle_center - 1), radius))
              }
            }
          }
          point = Vector2(point.x + polarity * cos(angle * PI / 180.0), point.y + polarity * sin(angle * PI / 180))
        }

      }
    }
  }
}
