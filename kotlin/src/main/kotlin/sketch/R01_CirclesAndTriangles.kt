package sketch

import kotlinx.cli.*

import org.openrndr.KEY_TAB
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
import kotlin.collections.max
import util.randomPointOnCircle

const val MAX_NUM_CONTOURS = 50
const val MAX_NUM_ARCS = 20
const val MAX_NUM_CIRCLES = 5
const val MIN_NUM_CIRCLES = 1
const val MIN_CIRCLE_RADIUS_PERCENT = .05
const val MAX_CIRCLE_RADIUS_PERCENT = .5

// Contour Generation Parameters
const val PROBABILITY_OUTSIDE_CIRCLE = 0.2
const val PROBABILITY_AT_CENTER = 0.2
const val PROBABILITY_A_CONTOUR_ARCS = 0.1
const val PROBABILITY_CONTOUR_SWEEP_IS_TRUE = 0.9

fun randomPointBasedOnCircle(
  circle: Circle,
  other_angles: List<Double>,
  probability_outside_circle: Double = PROBABILITY_OUTSIDE_CIRCLE,
  probability_at_center: Double = PROBABILITY_AT_CENTER,
  min_outside_radius: Double = 10.0,
  max_outside_radius: Double = 100.0
): Pair<Vector2, Double> {
  // Add a third point in between the other two points (between min and max angles
  val angles = other_angles
  val min_angle: Double = angles.minOrNull()!!
  val max_angle: Double = angles.maxOrNull()!!
  val last_angle = Random.double(min_angle, max_angle)

  return if (Random.bool(probability_outside_circle)) {
    Pair(
      circle.center + Vector2.fromPolar(
        Polar(
          last_angle,
          Random.double(circle.radius + min_outside_radius, circle.radius + max_outside_radius)
        )
      ), last_angle
    )
  } else if (Random.bool(probability_at_center)) { // At the center
    Pair(circle.center, last_angle)
  } else {
    // Somewhere in the circle nominally near the center, but with some std deviation around that
    Pair(
      circle.center + Vector2.fromPolar(
        Polar(
          last_angle,
          Random.gaussian(circle.radius * .25, 200.0)
        )
      ), last_angle
    )
  }
}

fun main(args: Array<String>) = application {
  // Setup argument parsing
  val parser = ArgParser("sketch")
  val width_arg by parser.option(ArgType.Int,  shortName = "w", description = "width").default(1000)
  val height_arg by parser.option(ArgType.Int, shortName = "e", description = "height").default(1000)
  val seed by parser.option(ArgType.Int, shortName = "s", description = "seed").default(0)

  parser.parse(args)

  configure {
    width = width_arg // Width of picture
    height = height_arg // Height of picture
  }

  program {
    // One time setup
    val max_dimension = arrayOf(width, height).maxOrNull()!!

    // Setup the seed value
    Random.randomizeSeed()
    Random.rnd = kotlin.random.Random(seed)

    // Initialize the lists of circles and arcs
    var base_circles = listOf<Circle>()
    var contours = mutableListOf<ShapeContour>()
    var arcs = mutableListOf<ShapeContour>()
    var is_complete = false // Is the shape drawing complete?

    // Initialize the numbers of contours arcs and circles
    var num_contours = 0
    var num_arcs = 0
    var num_circles: Int

    var is_paused = false
    var wait_frames = 0
    // Do we draw the circle outlines or not?
    var is_debug_draw_circles = false

    /**
     * Internal function for what we do to reset the drawing, this means regenerating new contours and arcs
     */
    fun resetDrawing() {
      // Calculate how many contours and arcs we want to have
      num_contours = Random.int(0, MAX_NUM_CONTOURS)
      num_arcs = Random.int(0, MAX_NUM_ARCS)
      num_circles = Random.int(MIN_NUM_CIRCLES, MAX_NUM_CIRCLES)
      is_complete = false
      contours.clear()
      arcs.clear()

      base_circles = (1..num_circles).map {
        val radius = Random.double(MIN_CIRCLE_RADIUS_PERCENT * max_dimension, MAX_CIRCLE_RADIUS_PERCENT * max_dimension)
        val center = Vector2(max_dimension * .5, max_dimension * .5) + Random.vector2(-max_dimension * .3, max_dimension * .3)
        Circle(center, radius)
      }
    }

    // Setup the picture for presentation mode which will go to the next
    // iteration on button press
//    window.presentationMode = PresentationMode.AUTOMATIC
    mouse.buttonUp.listen {
      resetDrawing()
      window.requestDraw()
    }

    // If we hit tab toggle the debug circles
    keyboard.keyUp.listen {
      if (it.name == "c") {
        is_debug_draw_circles = !is_debug_draw_circles
      } else if (it.name == "p") {
        is_paused = !is_paused
      }
    }


    // Finish initializing hte drawing
    resetDrawing()
    var camera = Screenshots()
    // Take a timestamped screenshot with the space bar
    extend(camera)
    extend {
      if (contours.size < num_contours) {
        // Choose a base circle
        var base_circle = base_circles.random(Random.rnd)

        // Choose 2 points on the circle
        var points_and_angles = mutableListOf<Pair<Vector2, Double>>()
        points_and_angles.add(randomPointOnCircle(base_circle))
        points_and_angles.add(randomPointOnCircle(base_circle, points_and_angles[0].second, 90.0))

        // Choose a third point which is somewhere between the other two points, possibly at the center of the circle
        // and possibly outside the circle
        val angles = points_and_angles.map { it -> it.second }

        // Determine if the last point will be outside of the circle or not
        points_and_angles.add(
          randomPointBasedOnCircle(base_circle, angles)
        )

        // draw the contours
        val points = points_and_angles.map { it -> it.first }
        contours.add(contour {
          moveTo(points.first())

          // Determine of the the two points that are on the circle will arc or not
          val starting_idx = if (Random.bool(PROBABILITY_A_CONTOUR_ARCS)) {
            // If we are arcing, are we sweeping outside or inside the circle? TRUE will trace the outside of hte circle
            val sweep_rand = Random.bool(PROBABILITY_CONTOUR_SWEEP_IS_TRUE)
            arcTo(base_circle.radius, base_circle.radius, 0.0, false, sweep_rand, points[1])
            1
          } else {
            0
          }

          // For all of the remaining points straight line to the point
          for (pt in points.subList(starting_idx, points.lastIndex + 1)) {
            lineTo(pt)
          }

          // Then close up the  line
          lineTo(anchor)
          close()
        })
      } else if (arcs.size < num_arcs) {
        // Let's also generate arcs, we will modulate the radii and also warp them a little bit with some probability
        // to make them more interesting
        var base_circle = base_circles.random(Random.rnd)


        // add some noise to where the center of the key is
        var center = base_circle.center + Random.vector2(-(2.0 / 1000) * max_dimension, (2.0 / 1000) * max_dimension)
        // Add noise to the radius with a gaussian distribution
        var radius = Random.gaussian(base_circle.radius, (10.0/ 10000) * max_dimension)

        // Generate a random arc with an angle somewhere on the circle
        var angles = mutableListOf<Double>(Random.double(-180.0, 180.0))
        angles.add(angles.first() + Random.double(10.0, 120.0))

        val pts = angles.map { angle ->
          center + Vector2.fromPolar(Polar(angle, radius))
        }

        arcs.add(contour {
          moveTo(pts[0])
          arcTo(
            base_circle.radius * Random.double(.95, 1.05),
            base_circle.radius * Random.double(.95, 1.05),
            0.0,
            false,
            true,
            pts[1]
          )
        })
      } else {
        is_complete = true
      }

      // Finally lets draw the results
      drawer.isolated {
        drawer.fill = ColorRGBa.TRANSPARENT
        drawer.strokeWeight = (3.0 / 1000) * max_dimension
        drawer.stroke = ColorRGBa.WHITE

        drawer.contours(contours)
      }

      drawer.isolated {
        drawer.fill = ColorRGBa.TRANSPARENT
        drawer.strokeWeight = (10.0 / 1000) * max_dimension
        drawer.stroke = ColorRGBa.WHITE

        drawer.contours(arcs)
      }

      if (is_debug_draw_circles) {
        drawer.isolated {
          drawer.strokeWeight = (2.0 / 1000) * max_dimension
          drawer.fill = ColorRGBa.TRANSPARENT
          drawer.stroke = ColorRGBa.PINK
          drawer.circles(base_circles)
        }
      }

      // If we are complete and paused, don't do anything
      // If we are complete and not paused take a screenshot, this involves waiting for a frame or two
      // before resetting the drawing
      if (is_complete) {
        if (!is_paused) {
          if (wait_frames == 0) {
            camera.trigger()
            wait_frames += 1
          } else
            if (wait_frames >= 5) {
              resetDrawing()
              wait_frames = 0
            } else {
              wait_frames += 1
            }
        }
      }
    }
  }
}

/// Curves next steps, add colors
