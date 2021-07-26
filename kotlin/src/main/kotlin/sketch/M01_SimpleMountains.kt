package sketch

import kotlinx.cli.*

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.Random
import org.openrndr.math.Polar
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.contour
import util.randomPointOnCircle
import util.DrawingStateManager
import org.openrndr.PresentationMode
import org.openrndr.draw.Drawer
import org.openrndr.extra.noise.random
import org.openrndr.extra.olive.oliveProgram
import util.clip
import kotlin.math.*


fun main(args: Array<String>) = application {
  // Setup argument parsing
  val parser = ArgParser("sketch")
  val width_arg by parser.option(ArgType.Int, fullName = "width", shortName = "w", description = "width (px)")
    .default(512)
  val height_arg by parser.option(ArgType.Int, fullName = "height", shortName = "e", description = "height (px)")
    .default(512)
  val seed by parser.option(ArgType.Int, shortName = "s", description = "seed").default(0)
  val _max_iterations by parser.option(ArgType.Int, shortName = "n", description = "Number of iterations").default(-1)

  parser.parse(args)

  configure {
    width = width_arg // Width of picture
    height = height_arg // Height of picture
  }

  oliveProgram {
    class MountainAttractor(center: Double, magnitude: Double, width: Int, K: Double = 5.0, N: Double = 1.0) {
      val center = center
      val magnitude = magnitude
      val width = width
      val K: Double = K
      val N: Double = N
      val list = generate(width)

      fun get(x: Double): Double {
        if (x < 0) {
          return list.first()
        } else if (x < list.size) {
          // TODO: add interpolation
          return list[x.toInt()]
        } else {
          return list.last()
        }
      }

      private fun generate(width: Int): List<Double> {
        val magnitudes =  (0..width+1)
          .map { x -> abs(center - x) / width.toDouble() }
          .map { err -> (1.0 - K * err.pow(N))}
          .map { it -> clip(it, 0.0, 1.0) }
          .map { it -> it * magnitude}

        val magnitudes_future = magnitudes.subList(1, magnitudes.lastIndex)
        val magnitudes_past = magnitudes.subList(0, magnitudes.lastIndex-1)
        val derivative = magnitudes_past.zip(magnitudes_future).map { (x0, x1) -> x1 - x0}
        return derivative // TODO: How to scale this properly?

        // Combine all of these together in one step! Do I even really need this method of doing it?
        // Can't I just mix them all
      }

      fun debug_draw(drawer: Drawer, mid_point: Double, scaler: Double) {
        for (x in (0..width-1)) {
          if (magnitude > 0) {
            drawer.fill = ColorRGBa.RED
          } else {
            drawer.fill = ColorRGBa.BLUE
          }
          val magnitude =  get(x.toDouble()) * scaler
          drawer.circle(x.toDouble(), mid_point - magnitude, 3.0)
        }
      }
    }
//    val max_dimension = arrayOf(width, height).maxOrNull()!!
    val STD_DEVIATION_OF_NUM_PEAK_ATTRACTORS = .1
    val STD_DEVIATION_OF_NUM_VALLEY_ATTRACTORS = .05

    var state_manager = DrawingStateManager()
    state_manager.max_iterations = 0

    // Setup the seed value
//    Random.rnd = kotlin.random.Random(seed)

    // Setup the picture for presentation mode which will go to the next
    // iteration on button press
//    window.presentationMode = PresentationMode.AUTOMATIC
    mouse.buttonUp.listen {
      state_manager.reset()
      window.requestDraw()
    }

    // Setup listener events for turning on and off debug mode or pausing
    //   d -> toggle debug mode
    //   p -> toggle paused
    keyboard.keyUp.listen {
      if (it.name == "d") {
        state_manager.is_debug = !state_manager.is_debug
      } else if (it.name == "p") {
        state_manager.is_paused = !state_manager.is_paused
      }
    }

    // Setup the state here
    var camera = Screenshots()



    // Generate the mountain attractors
    var num_peaks = Random.int(3, 8)
    var num_valleys = Random.int(1, 6)
    var min_magnitude = 10.0
    var max_magnitude = 100.0
    var attractors: MutableList<MountainAttractor> = mutableListOf()

    var mountain_mean_min_percent = 0.2
    var mountain_mean_max_percent = 0.8

    var mountain_point = Vector2(0.0, Random.double(height*mountain_mean_min_percent, height*mountain_mean_max_percent))
    var mountain_trail = mutableListOf<Vector2>()

    /**
     * Internal function for what we do to reset the drawing, this means regenerating new contours and arcs
     */
    fun partial_reset() {
      num_peaks = Random.int(3, 8)
      num_valleys = Random.int(1, 6)

      attractors.clear()
      attractors.addAll((1..num_peaks).map {
        MountainAttractor(Random.double(0.0, width.toDouble()), Random.double(min_magnitude, max_magnitude), width,
          K = Random.double(.5, 5.0), N = Random.double(.5, 2.0))
      })

      attractors.addAll((1..num_valleys).map {
        MountainAttractor(Random.double(0.0, width.toDouble()), -1.0*Random.double(min_magnitude, max_magnitude), width,
          K = Random.double(.5, 5.0), N = Random.double(.5, 2.0))
      })
      mountain_point = Vector2(0.0, Random.double(height*.4, height*.6))
    }

    fun reset() {
      partial_reset()
      mountain_trail.clear()
    }

    state_manager.reset_fn = ::reset
    state_manager.reset()
    // Take a timestamped screenshot with the space bar
    val debug_region = 100.0 // bottom 20 pixels

    extend(camera)
    extend {
      if (state_manager.is_debug) {
        val top_debug_region = height - debug_region
        val mid_debug_region = height - debug_region * .5
        drawer.isolated {
          drawer.stroke = ColorRGBa.PINK
          drawer.lineSegment(Vector2(0.0, mid_debug_region), Vector2(width.toDouble(), mid_debug_region))
          drawer.lineSegment(Vector2(0.0, top_debug_region), Vector2(width.toDouble(), top_debug_region))
        }
          drawer.isolated {
            for (attractor in attractors) {
              val plotting_scaler = 10.0
              attractor.debug_draw(drawer, mid_debug_region, debug_region / (2.0 * plotting_scaler))
            }
          }
      }

      drawer.isolated {
        val debug_offset = if (state_manager.is_debug) { Vector2(0.0,  -1.0 * debug_region) } else { Vector2(0.0, 0.0) }
        drawer.fill = ColorRGBa.WHITE
        drawer.stroke = ColorRGBa.WHITE
        drawer.circle(Circle(mountain_point + debug_offset, 10.0))
        drawer.circles(mountain_trail.map { pt -> Circle(pt + debug_offset, 5.0) })
        val dy = attractors.map { a -> -1.0 * a.get(mountain_point.x) }.sum()
        mountain_point += Vector2(1.0, dy)
        mountain_trail.add(mountain_point)

        if (mountain_point.x >= width) {
          partial_reset()
        }
      }
    }
  }
}
