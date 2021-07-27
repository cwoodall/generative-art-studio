package sketch

import kotlinx.cli.*
import org.openrndr.PresentationMode

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.LineCap
import org.openrndr.draw.StencilStyle
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.Random
import util.DrawingStateManager
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.videoprofiles.GIFProfile
import org.openrndr.extra.videoprofiles.ProresProfile
import org.openrndr.ffmpeg.MP4Profile
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.LinearRange
import org.openrndr.shape.*
import palettes.PaletteTwilight
import palettes.Palette_00
import techniques.BaseMountainAttractor
import techniques.MountainAttractor
import techniques.SumOfMountainAttractors
import kotlin.math.pow


fun main(args: Array<String>) = application {
  // Setup argument parsing
  val parser = ArgParser("sketch")
  val width_arg by parser.option(ArgType.Int, fullName = "width", shortName = "w", description = "width (px)")
    .default(1024)
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
//    val max_dimension = arrayOf(width, height).maxOrNull()!!
    var state_manager = DrawingStateManager()
    state_manager.max_iterations = 10
    var flyby = true
    var draw_as_rectangles = true

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
      } else if (it.name == "r") {
        draw_as_rectangles = !draw_as_rectangles
      } else if (it.name == "f") {
        flyby = !flyby
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
    // Take a timestamped screenshot with the space bar
    val debug_region = 100.0 // bottom 20 pixels

    val colors = PaletteTwilight()
    // Setup the state here
    var camera = Screenshots()
    extend(camera)
    extend(ScreenRecorder()) {
      profile = MP4Profile()
    }
    var last_second = seconds
    var first = true
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

        if (mountain_shapes.size > 10) {
          mountain_shapes.removeAt(0)
        }
        last_second = seconds
        first = false
      }

      drawer.isolated {
        drawer.fill = colors.background()
        drawer.stroke = colors.background()
        drawer.rectangle(0.0, 0.0, width.toDouble(), height.toDouble())
        var i = 0
        for ((s, c) in mountain_shapes.reversed()) {
          if (flyby) {
            drawer.translate(0.0, i.toDouble().pow(2) * height / 40.0)
          }
          if (draw_as_rectangles) {
            drawer.fill = c
            drawer.stroke = ColorRGBa.BLACK
            drawer.strokeWeight = 0.0
            val num_points = 200
//            val sample_points = .map { x -> Vector2(x, 0.0) }

            val points = (0.0..1.0 step 1.0 / num_points).map {
              it -> s.position(it)
            }
            val rect_width = width.toDouble() / (num_points)
//            drawer.circles(points, 10.0)
            drawer.rectangles(points
              .map { it ->
                Rectangle(it.x, it.y, rect_width, height - it.y)
              }
            )
//            val rects = points.map { pt -> Rectangle(pt.x-rect_width*.5, height.toDouble(), rect_width*.5, pt.y) }
//            drawer.rectangles(rects)
          } else {
            drawer.fill = c
            drawer.stroke = ColorRGBa.BLACK
            drawer.strokeWeight = 2.0
            drawer.contour(s)
          }
          i++
        }
      }
    }

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
          attractor.debugDraw(drawer, mid_debug_region, debug_region / (2.0))
        }
        drawer.fill = ColorRGBa.PINK
        sum_attractor.debugDraw(drawer, mid_debug_region, debug_region / (2.0 * MAX_MAGNITUDE))
      }

    }
    state_manager.postUpdate(camera)
  }
}

