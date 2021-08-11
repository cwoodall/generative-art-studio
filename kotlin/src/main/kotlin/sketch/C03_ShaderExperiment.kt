package sketch

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.ShadeStyle
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.videoprofiles.GIFProfile
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.Vector2
import org.openrndr.math.Vector4
import org.openrndr.shape.Circle
import org.openrndr.shape.Rectangle
import palettes.BasePalette
import util.*

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
            
            // Setting the stroke alpha to 1 prevents the weird smoothing effect that was causing issues at the
            // edges
            strokeAlpha = 1.0;
            """.trimMargin()
  }
}

@OptIn(ExperimentalStdlibApi::class)
fun main(args: Array<String>) = application {
  // Setup argument parsing
  val parser = ArgParser("sketch")
  val width_arg by parser.option(ArgType.Int, fullName = "width", shortName = "w", description = "width (px)")
    .default(1000)
  val height_arg by parser.option(ArgType.Int, fullName = "height", shortName = "e", description = "height (px)")
    .default(1000)
  val seed by parser.option(ArgType.Int, shortName = "s", description = "seed").default(0)
  val _max_iterations by parser.option(ArgType.Int, shortName = "n", description = "Number of iterationCount").default(-1)

  parser.parse(args)

  configure {
    width = width_arg // Width of picture
    height = height_arg // Height of picture
  }

  program {
    val max_dimension = arrayOf(width, height).maxOrNull()!!
    val rhWidth = 50.0
    val rhHeight = 20.0
    val rhDx = -15.0
    val drawingType = DrawingStyle.FILL_OUTLINE_OFF

    var state_manager = DrawingStateManager()
//    state_manager.maxIterations = _max_iterations

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
        state_manager.isDebug = !state_manager.isDebug
      } else if (it.name == "p") {
        state_manager.isPaused = !state_manager.isPaused
      }
    }

    val palette = palettes.PaletteTwilight()

    /**
     * Internal function for what we do to reset the drawing, this means regenerating new contours and arcs
     */
    fun reset() {
    }
    state_manager.resetHandler = ::reset
    state_manager.reset()

    // Take a timestamped screenshot with the space bar

    var backgroundShapes = buildList {
      forEachPixelInImage(width.toDouble(), height.toDouble(), rhWidth, rhHeight).forEach {
        val shapeColorIndex = (it.y.toInt() / rhHeight.toInt()) % 2
        val dxSign = if (shapeColorIndex == 0) 1.0 else -1.0
        val xOffset = if (shapeColorIndex == 0) 0.0 else rhDx
        add(
          ColorIndexedShape(
            makeRhombus(Vector2(it.x + xOffset, it.y), rhWidth - 1.9, rhHeight - 2, dxSign * rhDx).shape,
            ((it.x.toInt() / rhWidth.toInt()) + shapeColorIndex) % 2 + 2
          )
        )
      }
    }

    var ciShapes = listOf(
      ColorIndexedShape(Rectangle(0.0, 0.0, width * .2, height.toDouble()).shape, 1, Vector2(1.0, 0.0)),
      ColorIndexedShape(Rectangle(0.0, 0.0, width * .3, height.toDouble()).shape, 2, Vector2(.5, 0.0)),
      ColorIndexedShape(Rectangle(0.0, 0.0, width * 1.0, height * .3).shape, 3, Vector2(0.0, .5)),
      ColorIndexedShape(Rectangle(0.0, 0.0, width * 1.0, height * .2).shape, 4, Vector2(0.0, 2.0)),
      ColorIndexedShape(Circle(0.0, 0.0, 200.0).shape, 4, Vector2(2.0, 2.0))
    )

    val rt = renderTarget(width, height) {
      colorBuffer()
      depthBuffer()
    }

    var camera = Screenshots()
    extend(camera)
    extend(ScreenRecorder()) {
      GIFProfile()
    }
    extend {

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

        for (s in backgroundShapes + ciShapes) {
          drawer.stroke = ColorRGBa.BLACK
          drawer.strokeWeight = 1.0
          // TODO: instead of backwards engineering the color index we could store it directly
          // and then draw the color as well. So imagine using 2 color buffers, one which is actually
          // just being used to store state, the other which is drawing the present state of the image.
          drawer.shadeStyle = ShaderColorPaletteModMath(rt.colorBuffer(0), width, height, s.colorIndex, palette)
          drawer.shape(s.shape)
        }

      }
      drawer.image(rt.colorBuffer(0))

      ciShapes = ciShapes.map { s ->
        val shape = s.shape.transform(org.openrndr.math.transforms.transform {
          translate(s.velocity)
        })

        val center = shape.bounds.center
        var vel = if (center.x > width || center.y > height || center.x < 0.0 || center.y < 0.0) {
          s.velocity * -1.0
        } else {
          s.velocity
        }
        ColorIndexedShape(shape, s.colorIndex, vel)
      }.toMutableList()
    }
  }
}
