package sketch

import kotlinx.cli.*
import org.openrndr.PresentationMode
import org.openrndr.Program
import  org.openrndr.extras.camera.*
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.math.Vector2
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.Random
import util.DrawingStateManager
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extras.meshgenerators.extrudeShape
import org.openrndr.extras.meshgenerators.meshGenerator
import org.openrndr.ffmpeg.MP4Profile
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.Vector3
import org.openrndr.math.clamp
import org.openrndr.shape.*
import palettes.PaletteTwilight
import techniques.BaseMountainAttractor
import techniques.MountainAttractor
import techniques.SumOfMountainAttractors

infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
  require(start.isFinite())
  require(endInclusive.isFinite())
  require(step > 0.0) { "Step must be positive, was: $step." }
  val sequence = generateSequence(start) { previous ->
    if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
    val next = previous + step
    if (next > endInclusive) null else next
  }
  return sequence.asIterable()
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
//    val max_dimension = arrayOf(width, height).maxOrNull()!!
    var state_manager = DrawingStateManager()
    state_manager.max_iterations = 10
    var flyby = true
    var draw_as_rectangles = true
    var NUM_RECTANGLES = 50

    // Setup the seed value
//    Random.rnd = kotlin.random.Random(seed)

    // Setup the picture for presentation mode which will go to the next
    // iteration on button press
//    window.presentationMode = PresentationMode.MANUAL

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
      } else if (it.name == "f") {
        flyby = !flyby
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
    var num_peaks: Int
    var num_valleys: Int
    val MIN_MAGNITUDE = 20.0
    val MAX_MAGNITUDE = 200.0
    var attractors: MutableList<BaseMountainAttractor> = mutableListOf()
    var sum_attractor = SumOfMountainAttractors(width, attractors)

    var mountain_offset = 0.0
    var mountain_shapes = mutableListOf<Pair<ShapeContour, ColorRGBa>>()

    /**
     * Internal function for what we do to reset the drawing, this means regenerating new contours and arcs
     */
    fun partial_reset() {
      num_peaks = Random.int(3, 8)
      num_valleys = Random.int(1, 6)

      attractors.clear()
      attractors.addAll((1..num_peaks).map {
        MountainAttractor(
          Random.double(0.0, width.toDouble()), Random.double(.1, 1.0), width,
          K = Random.double(.5, 5.0), N = Random.double(.5, 3.0)
        )
      })

      attractors.addAll((1..num_valleys).map {
        MountainAttractor(
          Random.double(0.0, width.toDouble()), -1.0 * Random.double(.1, 1.0), width,
          K = Random.double(.5, 5.0), N = Random.double(.5, 2.0)
        )
      })

      sum_attractor = SumOfMountainAttractors(width, attractors, Random.double(MIN_MAGNITUDE, MAX_MAGNITUDE))
      mountain_offset = Random.double(height * .2, height * .6)
    }

    fun reset() {
      partial_reset()
      mountain_shapes.clear()
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

    extend(Orbital()) {
      this.eye = Vector3(width*1.0, height*1.5, 1000.0)
      this.far = 9000000.0
    }
    var last_second = seconds
    var first = true
    var NUM_MOUNTAINS = 20
    extend {

      if (first || (seconds - last_second) >= .5) {
        if (!state_manager.is_paused) {
          val c = contour {
            val offset = Vector2(0.0, mountain_offset)
            val start = Vector2(0.0, sum_attractor.getMagnitude(0.0)) + offset
            moveTo(start)
            for (x in 1 until width) {
              continueTo(Vector2(x.toDouble(), sum_attractor.getMagnitude(x.toDouble())) + offset)
            }
            lineTo(width.toDouble(), height.toDouble())
            lineTo(0.0, height.toDouble())
            lineTo(start)
            close()
          }
          mountain_shapes.add(Pair(c, colors.random()))
          partial_reset()
        }

        if (mountain_shapes.size > NUM_MOUNTAINS) {
          mountain_shapes.removeAt(0)
        }
        last_second = seconds
        first = false
      }

      drawer.isolated {
        drawer.drawStyle.depthWrite = true
        drawer.drawStyle.depthTestPass = DepthTestPass.LESS_OR_EQUAL

        var i = 0
        for ((s, c) in mountain_shapes.reversed()) {
          drawer.translate(0.0, 0.0, 30.0)
          if (draw_as_rectangles) {
            drawer.fill = c
            drawer.stroke = ColorRGBa.BLACK
            drawer.strokeWeight = 0.0

            val points = (0.0..1.0 step 1.0 / NUM_RECTANGLES).map {
              it -> s.position(it)
            }
            val rect_width = width.toDouble() / (NUM_RECTANGLES-1)

            val rects = points
              .map { it ->
                Rectangle(it.x, 0.0, rect_width, it.y)
              }
            var mes = meshGenerator {
              rects.map {
                extrudeShape(it.shape, 30.0)
              }
            }

            drawer.isolated {
                drawer.vertexBuffer(mes, DrawPrimitive.TRIANGLES)
            }
          } else {
            drawer.fill = c
            drawer.stroke = ColorRGBa.BLACK
            drawer.strokeWeight = 2.0
            var mes = meshGenerator {
              extrudeShape(s.shape, 30.0)
            }
            drawer.vertexBuffer(mes, DrawPrimitive.LINES)
          }
          i++
        }
      }
    }

    state_manager.postUpdate(camera)
  }
}

