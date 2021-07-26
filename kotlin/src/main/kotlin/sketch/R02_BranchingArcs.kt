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

fun main(args: Array<String>) = application {
  // Setup argument parsing
  val parser = ArgParser("sketch")
  val width_arg by parser.option(ArgType.Int, fullName = "width", shortName = "w", description = "width (px)")
    .default(1000)
  val height_arg by parser.option(ArgType.Int, fullName = "height", shortName = "e", description = "height (px)")
    .default(1000)
  val seed by parser.option(ArgType.Int, shortName = "s", description = "seed").default(0)
  val _max_iterations by parser.option(ArgType.Int, shortName = "n", description = "Number of iterations").default(-1)

  parser.parse(args)

  configure {
    width = width_arg // Width of picture
    height = height_arg // Height of picture
  }

  program {
    val max_dimension = arrayOf(width, height).maxOrNull()!!

    var state_manager = DrawingStateManager()
    state_manager.max_iterations = _max_iterations

    // Setup the seed value
    Random.rnd = kotlin.random.Random(seed)


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
    val MIN_RADIUS_PERCENT = .01
    val MAX_RADIUS_PERCENT = .2
    val MARGIN_PERCENT = .1
    val step_angle = 5.0 // degrees
    // Need to represent a tree of circles and arcs here
    var is_in_arc = false
    var num_counts_in_arc = 0
    var num_branches = 0
    var circles = mutableListOf<Circle>()
    var intersection_circles = mutableListOf<Circle>()
    var arcs = mutableListOf<ShapeContour>()
    var direction = 0.0

    fun branch() {
      is_in_arc = false
      num_counts_in_arc = 0
      num_branches++
    }

    /**
     * Internal function for what we do to reset the drawing, this means regenerating new contours and arcs
     */
    fun reset() {
      circles.clear()
      intersection_circles.clear()
      arcs.clear()
      branch()
      num_branches = 0
    }
    state_manager.reset_fn = ::reset

    state_manager.reset()
    // Take a timestamped screenshot with the space bar
    var camera = Screenshots()
    extend(camera)
    extend {
      if (circles.count() > 100) { state_manager.is_complete = true }
      if (!state_manager.is_complete) {
        if (!is_in_arc) {
          // Add some circles
          var radius = Random.double(MIN_RADIUS_PERCENT * max_dimension, MAX_RADIUS_PERCENT * max_dimension)
          var margin_px = radius * (1 + MARGIN_PERCENT)

          val center = if (num_branches > 0) {
            // Extend the last arc
            val last_circle = circles.last()
            val PROBABILITY_INTERNAL_CIRLCE = .5
            val polarity = if (Random.bool(PROBABILITY_INTERNAL_CIRLCE)) {
              -1
            } else {
              1
            }
            radius = Random.double(last_circle.radius*.5, last_circle.radius * .9)
            var polar_endpoint = Polar.fromVector(arcs.last().segments.last().end - last_circle.center)
            Vector2.fromPolar(
              Polar(
                polar_endpoint.theta,
                polar_endpoint.radius + polarity * radius
              )
            ) + last_circle.center
          } else {
            // Add new origin circles
            Random.vector2(margin_px, max_dimension.toDouble() - margin_px)
          }

          circles.add(Circle(center, radius))
          is_in_arc = true
        } else {
          // add the arcs
          val circle = circles.last()
          val start_point: Vector2 =
            if (num_counts_in_arc == 0) {
              val PROBABILITY_DIRECTION_INWARD = .5
              direction = if (Random.bool(PROBABILITY_DIRECTION_INWARD)) {
                -1.0
              } else {
                1.0
              }
              if (num_branches == 0) {
                randomPointOnCircle(circle, 0.0, 180.0).first
              } else {
                arcs.last().segments.last().end
              }
            } else {
              arcs.last().segments.last().end
            }

          val end_point = start_point.rotate(direction * step_angle, circle.center)
          val new_arc = contour {
            moveTo(start_point)
            arcTo(
              circle.radius,
              circle.radius,
              0.0,
              false,
              direction > 0,
              end_point
            )
          }

          num_counts_in_arc++

          val BRANCH_PROBABILITY_PER_COUNTS_IN_ARC = 0.003
          if (Random.bool(num_counts_in_arc * BRANCH_PROBABILITY_PER_COUNTS_IN_ARC)) {
            branch()
          }

          val END_TREE_PROBABILITY = 0.00001

          if (circles.last().radius <= .01 * max_dimension || Random.bool(END_TREE_PROBABILITY)) {
            branch()
            num_branches = 0
          }

          // Collision detection code, does not detect all collisions, not 100% sure why (seems to be at low intersection
          // angles)
          var collision = false
          if (arcs.count() > 1) {
            for (arc in arcs.subList(0, arcs.lastIndex - 1)) {
              val intersections = new_arc.intersections(arc)
              if (intersections.count() > 0) {
                println("Collision!")
                intersection_circles.addAll(
                  intersections.map { it ->
                    Circle(it.position, 10.0)
                  }
                )
                branch()
                num_branches = 0
                collision = true
                break
              }
            }
          }

          if (!collision) {
            arcs.add(new_arc)
          } else {
            arcs.removeLastOrNull()
          }
        }

      }

      if (state_manager.is_debug) {
        drawer.isolated {
          drawer.fill = ColorRGBa.TRANSPARENT
          drawer.stroke = ColorRGBa.WHITE
          drawer.circles(circles)

          drawer.fill = ColorRGBa.BLUE
          drawer.stroke = ColorRGBa.BLUE

          drawer.circles(intersection_circles)

        }
      }

      drawer.isolated {
        drawer.fill = ColorRGBa.TRANSPARENT
        drawer.stroke = ColorRGBa.WHITE
        drawer.strokeWeight = (5.0 / 1000) * max_dimension
        drawer.contours(arcs)

      }
      state_manager.postUpdate(camera)
    }
  }
}
