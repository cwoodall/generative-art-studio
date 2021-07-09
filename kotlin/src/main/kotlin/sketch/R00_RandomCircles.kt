package sketch

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extra.noise.gaussian
import org.openrndr.extra.noise.random
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import java.io.File
import org.openrndr.shape.Rectangle
import org.openrndr.shape.Shape
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import util.timestamp

fun main() = application {
  configure {
    width = 1000
    height = 1000
  }

  program {
    val num_shapes = 5
    val min_rect_size = 1.0
    val max_rect_size = 1000.0
    val shapes = Vector<Shape>(num_shapes)
    val shape_fills = Vector<ColorRGBa>(num_shapes)
    val progName = this.name.ifBlank { this.window.title.ifBlank { "my-amazing-drawing" } }
    // This is out hi-res render target which we draw to, before scaling it for the screen "preview"
    val rt = renderTarget(
      width.toInt(),
      height.toInt(),
      multisample = BufferMultisample.Disabled
    ) { // multisample requires some weird copying to another color buffer
      colorBuffer()
      depthBuffer()
    }

    val colors = listOf(
      ColorRGBa.fromHex("FCB264"), // orange
      ColorRGBa.fromHex("8DC0E2"), // blue
      ColorRGBa.fromHex("C4B2E1"), // purple
      ColorRGBa.fromHex("CEE4F2"), // light blue
      ColorRGBa.fromHex("FEE1C3"), // light orange
      ColorRGBa.fromHex("E9E2F4"), // light purple
    )

    extend {
      // Draw the background
      drawer.isolatedWithTarget(rt) {
        drawer.rectangle(-10.0, -10.0, width.toDouble()+10, height.toDouble()+10)
      }
      for (i in 0..num_shapes) {
        val vec = Vector2(
          random(-this.width.toDouble() / 2, this.width.toDouble() * 2),
          random(-this.height.toDouble() / 2, this.height.toDouble() * 2)
        )
        val circle = Circle(
          vec.x, vec.y,
          gaussian((min_rect_size - max_rect_size / 2) + min_rect_size, 500.0)
        )
        drawer.isolatedWithTarget(rt) {
          drawer.fill = colors.random()
          drawer.stroke = ColorRGBa.TRANSPARENT
          drawer.shape(circle.shape)
        }
      }

      // wow... jpg is like 3x smaller than PNG without a substantial (perceptible) quality loss :thinking face:
      val targetFile = File("screenshots/$progName/${timestamp()}.jpg")
      targetFile.parentFile?.let { file ->
        if (!file.exists()) {
          file.mkdirs()
        }
      }
      rt.colorBuffer(0).saveToFile(targetFile, async = false)
    }
  }
}
