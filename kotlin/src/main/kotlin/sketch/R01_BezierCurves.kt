package sketch

import org.openrndr.KEY_TAB
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extra.noise.gaussian
import org.openrndr.extra.noise.random
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.extensions.Screenshots
import org.openrndr.PresentationMode
import org.openrndr.extra.noise.Random
import org.openrndr.extra.vfx.Contour
import org.openrndr.math.Polar
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.contour
import util.randomVector2
import kotlin.math.cos
import kotlin.math.sin

fun main() = application {
  configure {
    width = 1000 // Width of picture
    height = 1000 // Height of picture
  }

  program {
    Random.randomizeSeed()
    // One time setup
    val picture_center = Vector2(width*.5, height *.5)
    var base_circles = mutableListOf<Circle>()
    var contours = mutableListOf<ShapeContour>()
    var arcs = mutableListOf<ShapeContour>()

    var num_contours = 3
    var num_arcs = 3
    var num_circles = 3
    var draw_circle = true

    for (i in 1..num_circles) {
      val radius = Random.double(100.0, 500.0)
      val base_circle = Circle(picture_center + Random.vector2(-width*.4, width*.4), radius)
      base_circles.add(base_circle)
    }

    // Setup the picture for presentation mode which will go to the next
    // iteration on button press
    window.presentationMode = PresentationMode.AUTOMATIC
    mouse.buttonUp.listen {
      contours.clear()
      arcs.clear()
      base_circles.clear()

      num_contours = Random.int(0, 5)
      num_arcs = Random.int(0, 40)
      num_circles = Random.int(1, 5)

      for (i in 1..num_circles) {
        val radius = Random.double(100.0, 500.0)
        val base_circle = Circle(picture_center + Random.vector2(-100.0, 100.0), radius)
        base_circles.add(base_circle)
      }
      window.requestDraw()
    }
    keyboard.keyUp.listen {
      if (it.key == KEY_TAB) {
        draw_circle = !draw_circle
      }
    }

    // Take a timestamped screenshot with the space bar
    extend(Screenshots())
    extend {


      if (contours.size < num_contours) {
        var base_circle = base_circles.random(Random.rnd)
        var points = mutableListOf<Vector2>()
        var point = base_circle.center + Vector2.fromPolar(Polar(Random.double(-180.0, 180.0), base_circle.radius))
        points.add(point)
        var last_angle = Random.double(-180.0, 180.0)
        point = base_circle.center + Vector2.fromPolar(Polar(last_angle, base_circle.radius))
        points.add(point)

        // Dertermine if the last point will be outside of the circle or not
        val last_rand = Random.double(0.0, 1.0)
        if (last_rand < .2) {
          point = base_circle.center + Vector2.fromPolar(
            Polar(
              Random.gaussian(last_angle, 2.0),
              Random.double(base_circle.radius + 10.0, base_circle.radius + 100.0)
            )
          )
        } else if (last_rand < .4) {
          point = base_circle.center
        } else {
          point = base_circle.center + Vector2.fromPolar(
            Polar(
              Random.gaussian(last_angle, 2.0),
              Random.gaussian(base_circle.radius * .1, 200.0)
            )
          )
        }

        points.add(point)

        for (pt in points) {
          drawer.circle(pt, 20.0)
        }

      val c = contour {
        moveTo(points.first())

        var starting_idx = 0
        val arc_rand = Random.bool(.1)
        if (arc_rand) {
          starting_idx = 1
          val sweep_rand = Random.bool(.9)
          arcTo(base_circle.radius, base_circle.radius , 0.0 , false, sweep_rand, points[1])
        }
        for (pt in points.subList(starting_idx, points.lastIndex+1)) {

          lineTo(pt)
        }
        lineTo(anchor)
        close()
      }
      contours.add(c)
      } else if (arcs.size < num_arcs) {
        var base_circle = base_circles.random(Random.rnd)

        var angle = Random.double(-180.0, 180.0)
        var center = base_circle.center + Random.vector2(-2.0,2.0)
        var radius = Random.gaussian(base_circle.radius, 10.0)
        val pts = listOf(
          center + Random.vector2(-1.0,1.0) + Vector2.fromPolar(Polar(angle, radius)),
          center + Vector2.fromPolar(Polar(angle + Random.double(10.0, 120.0), radius))
        )

        val c = contour {
          moveTo(pts[0])
          arcTo(base_circle.radius * Random.double(.95, 1.05),base_circle.radius * Random.double(.95, 1.05),0.0,false, true, pts[1])
        }

        arcs.add(c)
      }

      drawer.fill = ColorRGBa.TRANSPARENT
      drawer.strokeWeight = 3.0
      drawer.stroke = ColorRGBa.WHITE

      drawer.contours(contours)

      drawer.fill = ColorRGBa.TRANSPARENT
      drawer.strokeWeight = 10.0
      drawer.stroke = ColorRGBa.WHITE

      drawer.contours(arcs)

      if (draw_circle) {
        drawer.strokeWeight = 2.0
        drawer.fill = ColorRGBa.TRANSPARENT
        drawer.stroke = ColorRGBa.PINK

        drawer.circles(base_circles)
      }

    }
  }
}

/// Curves next steps, add colors
