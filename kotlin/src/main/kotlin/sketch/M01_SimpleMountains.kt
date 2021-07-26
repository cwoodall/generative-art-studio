package sketch

import kotlinx.cli.*

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.Random
import util.DrawingStateManager
import org.openrndr.draw.Drawer
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.videoprofiles.GIFProfile
import org.openrndr.ffmpeg.ScreenRecorder
import techniques.BaseMountainAttractor
import util.clip
import kotlin.math.*
import techniques.MountainAttractor
import techniques.SumOfMountainAttractors


fun main(args: Array<String>) = application {
  // Setup argument parsing
  val parser = ArgParser("sketch")
  val width_arg by parser.option(ArgType.Int, fullName = "width", shortName = "w", description = "width (px)")
    .default(1024)
  val height_arg by parser.option(ArgType.Int, fullName = "height", shortName = "e", description = "height (px)")
    .default(1024)
  val seed by parser.option(ArgType.Int, shortName = "s", description = "seed").default(0)
  val _max_iterations by parser.option(ArgType.Int, shortName = "n", description = "Number of iterations").default(-1)

  parser.parse(args)

  configure {
    width = width_arg // Width of picture
    height = height_arg // Height of picture
  }

  oliveProgram {
//    val max_dimension = arrayOf(width, height).maxOrNull()!!
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

    // Generate the mountain attractors
    var num_peaks: Int
    var num_valleys: Int
    val MIN_MAGNITUDE = 1.0
    val MAX_MAGNITUDE = 150.0
    var attractors: MutableList<BaseMountainAttractor> = mutableListOf()
    var sum_attractor: SumOfMountainAttractors = SumOfMountainAttractors(width, attractors)

    var mountain_mean_min_percent = 0.2
    var mountain_mean_max_percent = 0.8

    var mountain_point =
      Vector2(0.0, Random.double(height * mountain_mean_min_percent, height * mountain_mean_max_percent))
    var mountain_trail = mutableListOf<Vector2>()

    /**
     * Internal function for what we do to reset the drawing, this means regenerating new contours and arcs
     */
    fun partial_reset() {
      num_peaks = Random.int(3, 8)
      num_valleys = Random.int(1, 6)

      attractors.clear()
      attractors.addAll((1..num_peaks).map {
        MountainAttractor(
          Random.double(0.0, width.toDouble()), Random.double(MIN_MAGNITUDE, MAX_MAGNITUDE), width,
          K = Random.double(.5, 5.0), N = Random.double(.5, 3.0)
        )
      })

      attractors.addAll((1..num_valleys).map {
        MountainAttractor(
          Random.double(0.0, width.toDouble()), -1.0 * Random.double(MIN_MAGNITUDE, MAX_MAGNITUDE), width,
          K = Random.double(.5, 5.0), N = Random.double(.5, 2.0)
        )
      })

      sum_attractor = SumOfMountainAttractors(width, attractors)
      mountain_point = Vector2(0.0, Random.double(height * .4, height * .6))
    }

    fun reset() {
      partial_reset()
      mountain_trail.clear()
    }

    state_manager.reset_fn = ::reset
    state_manager.reset()
    // Take a timestamped screenshot with the space bar
    val debug_region = 100.0 // bottom 20 pixels

    // Setup the state here
    var camera = Screenshots()
    extend(camera)
    extend(ScreenRecorder()) {
      profile = GIFProfile()
    }
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
            attractor.debug_draw(drawer, mid_debug_region, debug_region / (2.0 * MAX_MAGNITUDE))
          }
          drawer.fill = ColorRGBa.PINK
          sum_attractor.debug_draw(drawer, mid_debug_region, debug_region / (2.0 * sum_attractor.maxMagnitude()))
        }

      }

      drawer.isolated {
        val debug_offset = if (state_manager.is_debug) {
          Vector2(0.0, -1.0 * debug_region)
        } else {
          Vector2(0.0, 0.0)
        }
        drawer.fill = ColorRGBa.WHITE
        drawer.stroke = ColorRGBa.WHITE
        drawer.circle(Circle(mountain_point + debug_offset, 10.0))
        drawer.circles(mountain_trail.map { pt -> Circle(pt + debug_offset, 3.0) })
        val dy = sum_attractor.get(mountain_point.x) * -1
        mountain_point += Vector2(1.0, dy) + Vector2(0.0, Random.gaussian(0.0, 0.1))
        mountain_trail.add(mountain_point)

        if (mountain_point.x >= width) {
          partial_reset()
        }
      }
    }
  }
}
