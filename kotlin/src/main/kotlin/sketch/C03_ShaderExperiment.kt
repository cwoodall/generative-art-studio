package sketch

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.draw.shadeStyle
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.videoprofiles.GIFProfile
import org.openrndr.extras.camera.Orbital
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.Vector2
import org.openrndr.math.Vector4
import org.openrndr.shape.Rectangle
import util.DrawingStateManager
import kotlin.math.abs

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

    /**
     * Internal function for what we do to reset the drawing, this means regenerating new contours and arcs
     */
    fun reset() {
    }
    state_manager.reset_fn = ::reset
    state_manager.reset()
    // Take a timestamped screenshot with the space bar
    var camera = Screenshots()
    var offst = Vector2(0.0, 0.0)
    var dOffst = Vector2(10.0, 10.0)
    extend(camera)

    val rt = renderTarget(width, height) {
      colorBuffer()
    }

    extend(ScreenRecorder()) {
      GIFProfile()
    }
    extend {
      state_manager.postUpdate(camera)

      // Draw the background to the render target
      drawer.isolatedWithTarget(rt) {
        // Clear the buffer, otherwise the render target will maintain state
        drawer.clear(ColorRGBa.BLACK)
        drawer.shadeStyle = shadeStyle {
          fragmentTransform = """
            x_fill.rgb = vec3(1.0, 0.0, 0.0);
          """.trimIndent()
        }
        drawer.rectangle(0.0, 0.0, width * 0.5, height * 0.5)

        drawer.shadeStyle = shadeStyle {
          fragmentTransform = """
            x_fill.rgb = vec3(0.0, 0.0, 1.0);
          """.trimIndent()
        }
        drawer.rectangle(0.0, 0.0, width * 0.1, height * 0.1)

        drawer.shadeStyle = shadeStyle {
          fragmentTransform = """
            x_fill.rgb = vec3(0.0, 1.0, 0.0);
          """.trimIndent()
        }
        drawer.rectangle(width * .5, height * 0.5, width * 0.5, height * 0.5)
      }

      // Now we have some fun
      drawer.isolatedWithTarget(rt) {
        // Set up the shader so that we can lookup where we are inside of the texture.
        // There is a weird coordinate space issue that I don't fully understand, to solve this
        // I had to rotate and rescale the axis so that we can actually access the correct parts of the
        // underlying image.
        //
        // THen we can modify that pixel from within our shader, we can even leave the stroke on
        // so the shape gets an appropriate stroke
        var arrayTest = listOf<Vector4>(
          ColorRGBa.BLUE.toVector4(),
            ColorRGBa.GREEN.toVector4(),
            ColorRGBa.RED.toVector4(),
            ColorRGBa.PINK.toVector4()
        )

        // Thoughts instead of backwards engineering the color index we could store it directly
        // and then draw the color as well. So imagine using 2 color buffers, one which is actually
        // just being used to store state, the other which is drawing the present state of the image.
        drawer.shadeStyle = shadeStyle {
          fragmentTransform = """
            vec2 invSize = vec2(1/p_width, 1/p_height);
            // Reflect the axis so that the upper left hand corner is 0,0
            vec2 coord = vec2(0,1) - c_screenPosition.xy*invSize * vec2(-1,1);
            vec4 c = texture2D(p_colorBuffer, coord);
            int bg_idx = -1;
            for(int i = 0; i < p_palette_SIZE; i++) {
              if (c.rgb == p_palette[i].rgb) {
                bg_idx = i;
                break;
              } else {
                x_fill.rgb = c.rgb;
              }              
            }
            
            if (bg_idx >= 0) {
              int next_idx = (p_idx + bg_idx) % p_palette_SIZE;
              x_fill.rgb = p_palette[next_idx].rgb;
            } else {
              x_fill.rgb = c.rgb;
            }
            """.trimMargin()
          // Pass the working color buffer to use as a texture
          parameter("colorBuffer", rt.colorBuffer(0))

          // Use the width and height for scaling
          parameter("width", width.toDouble())
          parameter("height", height.toDouble())
          parameter("idx", 1)
          parameter("palette", arrayTest.toTypedArray())
        }
        drawer.rectangles(
          listOf(
            Rectangle(width * .5 - 50 + offst.x, height * .5 - 50 + offst.y, 100.0, 100.0),
            Rectangle(width * .5 - 50 + offst.x, height * .5 - 50 - offst.y, 100.0, 100.0),
            Rectangle(width * .5 - 50, height * .5 - 50, 100.0, 100.0)
          )
        )

      }
      drawer.image(rt.colorBuffer(0))

      offst += dOffst
      if (abs(offst.x) > width * .5) {
        dOffst *= -1.0
      }
    }

  }
}
