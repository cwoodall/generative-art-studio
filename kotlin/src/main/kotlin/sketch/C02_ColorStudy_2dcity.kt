package sketch

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.openrndr.application
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.ffmpeg.MP4Profile
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.Vector2
import org.openrndr.shape.*
import util.*
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
    val drawingType = DrawingStyle.FILL_OUTLINE_OFF

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

    fun makeRhombus(startingPosition: Vector2, width: Double, height: Double, dx: Double = 0.0, dy: Double = 0.0): ShapeContour {
      return contour {
        moveTo(startingPosition)
        lineTo(startingPosition.x + width, startingPosition.y + dy)
        lineTo(startingPosition.x + width + dx, startingPosition.y + height + dy)
        lineTo(startingPosition.x + dx, startingPosition.y + height)
        close()
      }
    }

    var backgroundShapes = mutableListOf<ColorIndexedShape>()

    var rhWidth = 50.0
    var rhHeight= 20.0
    var rhDx = -10.0
    for (x in 0.0..width.toDouble() step rhWidth) {
      for (y in 0.0..height.toDouble() step rhHeight) {
        val y_idx = (y.toInt() / rhHeight.toInt()) % 2
        var dxSign = 0.0
        var xOffset = 0.0
        if (y_idx == 0) {
          dxSign = 1.0
          xOffset = 0.0
        } else {
          dxSign = -1.0
          xOffset = rhDx
        }

        backgroundShapes.add(
          ColorIndexedShape(
            makeRhombus(Vector2(x+xOffset, y), rhWidth, rhHeight, dxSign*rhDx).shape,
            ((x.toInt() / rhWidth.toInt()) + y_idx) % 2 + 2
          )
        )
      }
    }

    var ciShapes = listOf(
      ColorIndexedShape(Rectangle(0.0,0.0, width * .2, height.toDouble()).shape, 1, Vector2(1.0, 0.0)),
      ColorIndexedShape(Rectangle(0.0,0.0, width * .3, height.toDouble()).shape, 2, Vector2(.5, 0.0)),
      ColorIndexedShape(Rectangle(0.0,0.0, width * 1.0, height * .3).shape, 3, Vector2(0.0, .5)),
      ColorIndexedShape(Rectangle(0.0,0.0, width * 1.0, height * .2).shape, 4, Vector2(0.0, 2.0))
    )


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

      // Move the canvas so we are centered about the point 0, 0
//      drawer.translate(width * .5, height * .5)

      if (isRotating) drawer.rotate(degrees++)

      val permutationIntersectionShapes: MutableList<ColorIndexedShape> = mutableListOf()
      val permutations = getAllUniqueCombinations(ciShapes)
      for (permutation in permutations.sortedBy { it.size }) {
        val inter = compound {
          intersection {
            permutation.map { shape(it.shape) }
          }
        }

        for (s in inter) {
          permutationIntersectionShapes.add(ColorIndexedShape(s, permutation.sumOf { it.colorIndex }, Vector2.ZERO))
        }
      }

      val intersectionShapes: MutableList<ColorIndexedShape> = mutableListOf()
      for (bgS in backgroundShapes) {
        for (fgS in ciShapes + permutationIntersectionShapes) {
          val inter = compound {
            intersection {
              shape(bgS.shape)
              shape(fgS.shape)
            }
          }

          for (s in inter) {
            intersectionShapes.add(ColorIndexedShape(s, bgS.colorIndex + fgS.colorIndex, Vector2.ZERO))
          }
        }
      }



      for (s in backgroundShapes + ciShapes + permutationIntersectionShapes + intersectionShapes) {
        util.setDrawingStyle(drawer, drawingType, palette.wrappedGet(s.colorIndex))
        drawer.shape(s.shape)
      }

      ciShapes = ciShapes.map { s ->
        val shape = s.shape.transform(org.openrndr.math.transforms.transform {
          translate(s.velocity)
        })

        val center = shape.bounds.center
        var vel = if (center.x > width || center.y > height || center.x < 0.0 || center.y < 0.0) {
          s.velocity * -1.0
        } else {
          s.velocity
        }
        ColorIndexedShape(shape, s.colorIndex, vel)
      }.toMutableList()
//      state_manager.postUpdate(camera)
    }
  }
}
