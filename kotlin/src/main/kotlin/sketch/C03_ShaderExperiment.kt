package sketch

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.openrndr.application
import org.openrndr.color.AlgebraicColor
import org.openrndr.color.ColorRGBa
import org.openrndr.color.ConvertibleToColorRGBa
import org.openrndr.draw.*
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.parameters.ColorParameter
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.parameters.Parameter
import org.openrndr.extra.videoprofiles.GIFProfile
import org.openrndr.extras.camera.Orbital
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.CastableToVector4
import org.openrndr.math.Vector2
import org.openrndr.math.Vector4
import org.openrndr.shape.Rectangle
import palettes.BasePalette
import util.DrawingStateManager
import kotlin.math.abs

open class ShaderColorPaletteModMath(
  colorBuffer: ColorBuffer,
  width: Int,
  height: Int,
  colorIdx: Int,
  palette: BasePalette
) : ShadeStyle() {

  var colorBuffer: ColorBuffer by Parameter()
  var width: Double by Parameter()
  var height: Double by Parameter()
  var colorIdx: Int by Parameter()
  var palette: Array<Vector4> by Parameter()

  init {
    this.colorBuffer = colorBuffer
    this.width = width.toDouble()
    this.height = height.toDouble()
    this.colorIdx = colorIdx
    this.palette = palette.toVector4Array()

    fragmentPreamble = """ """.trimMargin()
    fragmentTransform = """
            vec2 invSize = vec2(1/p_width, 1/p_height);
            // Reflect the axis so that the upper left hand corner is 0,0
            vec2 coord = vec2(0,1) - c_screenPosition.xy*invSize * vec2(-1,1);
            vec4 c = texture2D(p_colorBuffer, coord);
            int bg_idx = 0;
            for(int i = 0; i < p_palette_SIZE; i++) {
              if (c.rgb == p_palette[i].rgb) {
                bg_idx = i;
                break;
              } else if (c.rgb == vec3(0.0, 0.0, 0.0)) {
                bg_idx = -1;
                break;
              }
            }
            
            if (bg_idx >= 0) {
              int next_idx = (p_colorIdx + bg_idx) % p_palette_SIZE;
              x_fill.rgb = p_palette[next_idx].rgb;
            } else {
              x_fill.rgb = c.rgb;
            }
            """.trimMargin()
  }
}

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
    var offst = Vector2(0.0, 0.0)
    var dOffst = Vector2(-1.0, 1.0)
    extend(camera)

    val rt = renderTarget(width, height) {
      colorBuffer()
    }

    extend(ScreenRecorder()) {
      GIFProfile()
    }
    extend {
      state_manager.postUpdate(camera)

      // Now we have some fun
      drawer.isolatedWithTarget(rt) {
        drawer.clear(palette.background)

        // Set up the shader so that we can lookup where we are inside of the texture.
        // There is a weird coordinate space issue that I don't fully understand, to solve this
        // I had to rotate and rescale the axis so that we can actually access the correct parts of the
        // underlying image.
        //
        // THen we can modify that pixel from within our shader, we can even leave the stroke on
        // so the shape gets an appropriate stroke
        drawer.stroke = ColorRGBa.TRANSPARENT



        // TODO: instead of backwards engineering the color index we could store it directly
        // and then draw the color as well. So imagine using 2 color buffers, one which is actually
        // just being used to store state, the other which is drawing the present state of the image.
        drawer.shadeStyle = ShaderColorPaletteModMath(rt.colorBuffer(0), width, height, 1, palette)
        drawer.fill= palette.colors[1]
        drawer.rectangle(0.0, 0.0, width * 0.5, height * 0.5)

        drawer.fill = palette.colors[2]
        drawer.rectangle(0.0, 0.0, width * 0.1, height * 0.1)

        drawer.fill = palette.colors[3]
        drawer.rectangle(width * .5, height * 0.5, width * 0.5, height * 0.5)

        drawer.rectangle(
            Rectangle(width * .5 - 50 + offst.x, height * .5 - 50 + offst.y, 100.0, 100.0)
          )

        drawer.rectangle(Rectangle(width * .5 - 50 + offst.x, height * .5 - 50 - offst.y, 100.0, 100.0))
        drawer.rectangle(Rectangle(width * .5 - 50, height * .5 - 50, 100.0, 100.0))

      }
      drawer.image(rt.colorBuffer(0))

      offst += dOffst
      if (abs(offst.x) > width * .5) {
        dOffst *= -1.0
      }
    }

  }
}
