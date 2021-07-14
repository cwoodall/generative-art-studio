package sketch

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extra.noise.gaussian
import org.openrndr.extra.noise.random
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.extensions.Screenshots
import org.openrndr.PresentationMode
import palettes.Palette_00

fun main() = application {
  configure {
    width = 1000
    height = 1000
  }

  program {
    val num_shapes = 5
    val min_radius = 1.0
    val max_radius = 1000.0

    val colors = Palette_00()

    window.presentationMode = PresentationMode.MANUAL
    mouse.buttonUp.listen {
      window.requestDraw()
    }

    extend(Screenshots())
    extend {
      // Draw the background
      drawer.rectangle(-10.0, -10.0, width.toDouble() + 10, height.toDouble() + 10)
      for (i in 0..num_shapes) {
        val vec = Vector2(
          random(-this.width.toDouble() / 2, this.width.toDouble() * 2),
          random(-this.height.toDouble() / 2, this.height.toDouble() * 2)
        )
        val circle = Circle(
          vec.x, vec.y,
          gaussian((min_radius - max_radius / 2) + min_radius, 500.0)
        )
        drawer.fill = colors.random()
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.shape(circle.shape)
      }

//      // wow... jpg is like 3x smaller than PNG without a substantial (perceptible) quality loss :thinking face:
//      val targetFile = File("screenshots/$progName/${timestamp()}.jpg")
//      targetFile.parentFile?.let { file ->
//        if (!file.exists()) {
//          file.mkdirs()
//        }
//      }
//      rt.colorBuffer(0).saveToFile(targetFile, async = false)
//    }
    }
  }
}
