package sketch

import kotlinx.cli.*
import  org.openrndr.extras.camera.*
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.Random
import util.DrawingStateManager
import org.openrndr.extras.meshgenerators.extrudeShape
import org.openrndr.extras.meshgenerators.meshGenerator
import org.openrndr.ffmpeg.MP4Profile
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.*
import org.openrndr.shape.*
import palettes.PaletteTwilight
import techniques.MountainAttractor
import techniques.SumOfMountainAttractors
import util.linearSamplePositions

fun generateRandomMountainContour(
  width: Int,
  min_peaks: Int = 3,
  max_peaks: Int = 8,
  min_valleys: Int = 1,
  max_valleys: Int = 6,
  min_magnitude: Double = 20.0,
  max_magnitude: Double = 200.0
): SumOfMountainAttractors {
  val num_peaks = Random.int(min_peaks, max_peaks)
  val num_valleys = Random.int(min_valleys, max_valleys)

  val attractors = (0 until num_peaks + num_valleys).map {
    val sign = if (it < num_peaks) {
      1.0
    } else {
      -1.0
    }
    MountainAttractor(
      Random.double(0.0, width.toDouble()), sign * Random.double(.1, 1.0), width,
      K = Random.double(.5, 5.0), N = Random.double(.5, 3.0)
    )
  }

  return SumOfMountainAttractors(width, attractors, Random.double(min_magnitude, max_magnitude))
}


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

  program {
    var state_manager = DrawingStateManager()
    state_manager.max_iterations = _max_iterations

    var draw_as_rectangles = true
    var NUM_RECTANGLES = 50
    val DEPTH_3D = 30.0
    var NUM_MOUNTAINS = 20
    var UPDATE_RATE = .5

    // Setup the seed value
    Random.rnd = kotlin.random.Random(seed)

    // Setup listener events for turning on and off debug mode or pausing
    //   d -> toggle debug mode
    //   p -> toggle paused
    keyboard.keyUp.listen {
      if (it.name == "d") {
        state_manager.is_debug = !state_manager.is_debug
      } else if (it.name == "p") {
        state_manager.is_paused = !state_manager.is_paused
      } else if (it.name == "r") {
        draw_as_rectangles = !draw_as_rectangles
      } else if (it.name == "n") {
        window.requestDraw()
      } else if (it.name == "l") {
        state_manager.reset()
      } else if (it.name == "a") {
        NUM_RECTANGLES--
        NUM_RECTANGLES.clamp(1, 1000)
      } else if (it.name == "s") {
        NUM_RECTANGLES++
        NUM_RECTANGLES.clamp(1, 1000)
      }
    }

    // Generate the mountain attractors
    var mountainContours = mutableListOf<Pair<ShapeContour, ColorRGBa>>()


    /**
     * Internal function for what we do to reset the drawing, this means regenerating new contours and arcs
     */
    fun reset() {
      mountainContours.clear()
    }

    state_manager.reset_fn = ::reset
    state_manager.reset()

    val colors = PaletteTwilight()
    // Setup the state here
    var camera = Screenshots()
    extend(camera)
    extend(ScreenRecorder()) {
      profile = MP4Profile()
    }

    var orbital = Orbital()
    extend(orbital) {
      this.eye = Vector3(width * 1.0, height * 1.5, 500.0)
      this.far = 90000.0
      this.camera.setView(Vector3(x=133.45213935225578, y=43.55330124647086, z=-179.37208424047094), Spherical(theta=42.88086053564933, phi=70.3215365670353, radius=1639.778047219882), 90.0)
    }

    var lastTimestep = seconds
    var isFirst = true
    extend {
      if (!state_manager.is_paused && isFirst || (seconds - lastTimestep) >= UPDATE_RATE) {
        val mountainOutline = generateRandomMountainContour(width)

        // For capturing views that we want to keep
//        println(orbital.camera.lookAt)
//        println(orbital.camera.spherical)
//        println(orbital.camera.fov)
        // Draw the mountains contour
        val c = contour {
          val offset = Vector2(0.0, Random.double(height * .2, height * .6))
          val start = Vector2(0.0, mountainOutline.getMagnitude(0.0)) + offset
          moveTo(start)
          for (x in 1 until width) {
            continueTo(Vector2(x.toDouble(), mountainOutline.getMagnitude(x.toDouble())) + offset)
          }
          lineTo(width.toDouble(), height.toDouble())
          lineTo(0.0, height.toDouble())
          lineTo(start)
          close()
        }

        // assign a color to that contuor
        mountainContours.add(Pair(c, colors.random()))

        // If we have more than NUM_MOUNTAINS roll off the first mountain
        if (mountainContours.size > NUM_MOUNTAINS) {
          mountainContours.removeAt(0)
        }

        // Update state
        lastTimestep = seconds
        isFirst = false
      }

      // prepare to draw inside of the perspective veiw
      drawer.isolated {
        drawer.drawStyle.depthWrite = true
        drawer.drawStyle.depthTestPass = DepthTestPass.LESS_OR_EQUAL

        for ((mountainContour, mountainColor) in mountainContours.reversed()) {
          // Translate by the depth for this by reversing the mountain shapes list each older element will get closer
          // to the camera.
          drawer.translate(0.0, 0.0, DEPTH_3D)
          if (draw_as_rectangles) {
            // Create the fill for the mesh
            drawer.fill = mountainColor

            // Create all of the rectangles for extrusion and join them together into one mesh
            var layerMesh = meshGenerator {
              // Calculate the width of each rectangle
              val rectWidth = width.toDouble() / (NUM_RECTANGLES - 1)

              mountainContour
                .linearSamplePositions(NUM_RECTANGLES)
                .map { Rectangle(it.x, 0.0, rectWidth, it.y) }
                .map { extrudeShape(it.shape, DEPTH_3D) }
            }

            // And then draw the mesh
            drawer.vertexBuffer(layerMesh, DrawPrimitive.LINES)
          } else {
            // Draw the whole mountain. This is actually inverted for some reason.
            drawer.fill = mountainColor
            var mes = meshGenerator {
              extrudeShape(mountainContour.shape, DEPTH_3D)
            }
            drawer.vertexBuffer(mes, DrawPrimitive.TRIANGLES)
          }
        }
      }
    }

    state_manager.postUpdate(camera)
  }
}

