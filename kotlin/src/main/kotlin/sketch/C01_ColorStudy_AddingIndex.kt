package sketch

import kotlinx.cli.*

import org.openrndr.application
import org.openrndr.extensions.Screenshots
import util.DrawingStateManager
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.videoprofiles.GIFProfile
import org.openrndr.ffmpeg.MP4Profile
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.Vector2
import org.openrndr.shape.*
import kotlin.math.abs

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
    val max_dimension = arrayOf(width, height).maxOrNull()!!

    var state_manager = DrawingStateManager()
//    state_manager.max_iterations = _max_iterations

    // Setup the seed value
//    Random.rnd = kotlin.random.Random(seed)

    // Setup the picture for presentation mode which will go to the next
    // iteration on button press
//    window.presentationMode = PresentationMode.MANUAL
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

    class ColorIndexedShape(val s: Shape, val cI: Int, val vel: Vector2) {
      var shape: Shape = s
      var colorIndex = cI
      var velocity = vel
    }

    val palette = palettes.PaletteTwilight()

    /**
     * Internal function for what we do to reset the drawing, this means regenerating new contours and arcs
     */
    fun reset() {
    }
    state_manager.reset_fn = ::reset
    state_manager.reset()
    // Take a timestamped screenshot with the space bar
    var camera = Screenshots()


    var ciShapes = listOf(
      ColorIndexedShape(Circle(-width * .5, 0.0, 200.0).shape, 1, Vector2(1.0, 0.2)),
      ColorIndexedShape(Circle(+width * .5, 0.0, 100.0).shape, 2, Vector2(-0.5, 1.0)),
      ColorIndexedShape(Rectangle(-width * .5, -height * .5, width * 1.0, height * .1).shape, 3, Vector2(0.0, 1.2)),
      ColorIndexedShape(Rectangle(-width * .5, -height * .5, width * 1.0, height * .2).shape, 2, Vector2(0.0, +2.0)),
      ColorIndexedShape(Rectangle(-width * .5, -height * .5, width * 1.0, height * .4).shape, 2, Vector2(0.0, +0.5)),
      ColorIndexedShape(Circle(0.0, -height * .5, 100.0).shape, 4, Vector2(0.1, 1.1))
    )

    fun <T> getAllUniqueCombinations(x: List<T>, max_size: Int = x.size, min_size: Int = 2): Set<List<T>> {
      fun getAllUniqueCombinationsUpToSize(x: List<T>, n: Int): Set<List<T>> {
        when (n) {
          0 -> {
            return setOf()
          }
          1 -> {
            return x.map<T, List<T>> { listOf(it) }.toSet()
          }
          else -> {
            var prev = getAllUniqueCombinationsUpToSize(x, n - 1).toMutableList()
            var ret = mutableSetOf<List<T>>()
            for (i in x) {
              for (p in prev) {
                var list = p.toMutableSet()
                list.add(i)
                ret.add(list.toList())
              }
            }
            ret.addAll(prev)
            return ret
          }
        }
      }

      return getAllUniqueCombinationsUpToSize(x, max_size).filter { it.size >= min_size }.sortedBy { it.size }.toSet()
    }

    var degrees: Double = 0.0
    var isRotating = false
    extend(camera)
    extend(ScreenRecorder()) {
      profile = MP4Profile()
    }
    extend {
      // Clear the background
      drawer.clear(palette.background)

      // Set the stroke to BLACK
      drawer.stroke= ColorRGBa.BLACK

      // Move the canvas so we are centered about the point 0, 0
      drawer.translate(width * .5, height * .5)

      if (isRotating) drawer.rotate(degrees++)

      for (s in ciShapes) {
        drawer.fill = palette.wrappedGet(s.colorIndex)
        drawer.shape(s.shape)
      }

      val intersection_shapes = mutableListOf<ColorIndexedShape>()

      val permutations = getAllUniqueCombinations(ciShapes)
      for (permutation in permutations.sortedBy { it.size }) {
        val inter = compound {
          intersection {
            permutation.map { shape(it.shape) }
          }
        }

        for (s in inter) {
          intersection_shapes.add(ColorIndexedShape(s, permutation.map { it.colorIndex }.sum(), Vector2.ZERO))
        }
      }

      for (s in intersection_shapes) {
        drawer.fill = palette.wrappedGet(s.colorIndex)
        drawer.stroke= ColorRGBa.BLACK
        drawer.shape(s.shape)
      }

      ciShapes = ciShapes.map { s ->
        val shape = s.shape.transform(org.openrndr.math.transforms.transform {
          translate(s.velocity)
        })

        val center = shape.bounds.center
        var vel = if (abs(center.x) > width * .5 || abs(center.y) > height * .5) {
          s.velocity * -1.0
        } else {
          s.velocity
        }
        ColorIndexedShape(shape, s.colorIndex, vel)
      }
//      state_manager.postUpdate(camera)
    }
  }
}
