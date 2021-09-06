package sketch.g03

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.Random
import org.openrndr.ffmpeg.MP4Profile
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.Vector2
import org.openrndr.shape.*
import org.openrndr.svg.saveToFile
import palettes.*
import util.DrawingStateManager

import techniques.space_colonization.*
import java.io.File


enum class TreeShapes(val value: Int) {
  Circle(0),
  Triangle(1);

  companion object {
    private val VALUES = values()
    fun getByValue(value: Int) = VALUES.firstOrNull { it.value == value }
  }
}

class BlackBG_White : BasePalette() {
  override val colors = listOf(
    ColorRGBa.WHITE,
  )

  override val background = ColorRGBa.BLACK

  override fun background(): ColorRGBa {
    return background
  }
  override fun random(): ColorRGBa {
    return colors.random()
  }
}

class WhiteBG_Black : BasePalette() {
  override val colors = listOf(
    ColorRGBa.BLACK,
  )

  override val background = ColorRGBa.WHITE

  override fun background(): ColorRGBa {
    return background
  }
  override fun random(): ColorRGBa {
    return colors.random()
  }
}


fun main(args: Array<String>) = application {
  // Setup argument parsing
  val parser = ArgParser("sketch")
  val width_arg by parser.option(ArgType.Int, fullName = "width", shortName = "w", description = "width (px)")
    .default(1250)
  val height_arg by parser.option(ArgType.Int, fullName = "height", shortName = "e", description = "height (px)")
    .default(1250)
  // 9432
  val seed by parser.option(ArgType.Int, shortName = "s", description = "seed").default(1223)
  val _max_iterations by parser.option(ArgType.Int, shortName = "n", description = "Number of iterationCount")
    .default(0)
  val monoline by parser.option(ArgType.Boolean, shortName = "m", description = "Grow at a rate or force monoline")
    .default(false)
  val quitOnCompletion by parser.option(
    ArgType.Boolean,
    shortName = "q",
    description = "Quite when completed wiht given iterations"
  ).default(false)

  parser.parse(args)

  configure {
    width = width_arg // Width of picture
    height = height_arg // Height of picture
  }

  program {
    var stateManager = DrawingStateManager()
    stateManager.maxIterations = _max_iterations
    stateManager.isDebug = false
    stateManager.onCompletionHandler = {
      if (quitOnCompletion) program.application.exit()
    }

    // Setup the seed value
    Random.rnd = kotlin.random.Random(seed)
    val CENTER_OF_IMAGE = Vector2(width * .5, height * .5)
    val growthRate = if (monoline) 0.0 else 0.0008

    var theTree: Network = Network(
      Rectangle.fromCenter(CENTER_OF_IMAGE, width.toDouble(), height.toDouble()),
      growthRate = growthRate
    )

    var theRoots: Network = Network(
      Rectangle.fromCenter(CENTER_OF_IMAGE, width.toDouble(), height.toDouble()),
      growthRate = growthRate
    )


    val palettes: Array<BasePalette> = arrayOf(
      WhiteBG_Black(),
      BlackBG_White(),
    )
    var palette: BasePalette = Palette_00()

    var recorder = ScreenRecorder()
    recorder.maximumDuration = 3000.0
    var camera = Screenshots()
    var contours = mutableListOf<ShapeContour>()
    var rootContours = mutableListOf<ShapeContour>()

    /**
     * Internal function for what we do to reset the drawing, this means regenerating new contours and arcs
     */
    fun reset() {
      palette = palettes[Random.int0(palettes.size)]
      theTree.reset()
      theRoots.reset()
      contours.clear()
      rootContours.clear()

      // Generate the Tree Shape
      val shape_type = TreeShapes.getByValue(Random.int0(TreeShapes.values().size))

      val MIN_TREE_HEIGHT = 25.0
      val MAX_TREE_HEIGHT = 200.0
      val STEM_HEIGHT_MIN = 5.0
      val STEM_HEIGHT_MAX = 20.0

      val tree_height = Random.double(MIN_TREE_HEIGHT, MAX_TREE_HEIGHT)
      val outer_tree_width = Random.double(200.0, 600.0)
      val tree_layers = Random.int(5, 20)
      val tree_teirs_distance = Random.double(10.0, 30.0)
      val tree_height_ratio = Random.double0(2.0)
      contours.addAll(
        (0 until tree_layers).map {
          val tree_teirs_distance = Random.double(.8, 1.2) * outer_tree_width / tree_layers
          var layer_width = outer_tree_width - it*tree_teirs_distance
          when (shape_type) {
            TreeShapes.Circle -> {
              val circle = Circle(CENTER_OF_IMAGE - Vector2(0.0, tree_height), layer_width*.5).contour
              val split_line = LineSegment(-1.0*width, CENTER_OF_IMAGE.y-tree_height, width *2.0, CENTER_OF_IMAGE.y - tree_height).contour
              circle.split(split_line).first()

            }
            TreeShapes.Triangle -> {
              val shape = Triangle(
                CENTER_OF_IMAGE + Vector2(-layer_width*.5, tree_height),
                CENTER_OF_IMAGE + Vector2(layer_width*.5, tree_height),
                CENTER_OF_IMAGE + Vector2(0.0, -tree_height-layer_width*tree_height_ratio)
              ).contour
              val split_line = LineSegment(0.0, CENTER_OF_IMAGE.y-tree_height, width.toDouble(), CENTER_OF_IMAGE.y - tree_height).contour
              shape.split(split_line).first()
            }
            else -> Circle(CENTER_OF_IMAGE + Vector2(0.0, -tree_height), layer_width).contour
          }
        }
      )

      // Stem
      val stem_height = Random.double(STEM_HEIGHT_MIN, STEM_HEIGHT_MAX)
      val centerOfTree = CENTER_OF_IMAGE + Vector2(0.0, -tree_height)
      contours.add(
        LineSegment(
          centerOfTree - Vector2.UNIT_Y * stem_height,
          centerOfTree + Vector2.UNIT_Y * stem_height
        ).contour)

      // Generate the Roots
      val number_shapes = Random.int(5, 30)
      rootContours.addAll((0 until number_shapes).map {
        val center_pt = Vector2(
          Random.double(-.25*width, 0.25*width) + CENTER_OF_IMAGE.x,
          Random.double(CENTER_OF_IMAGE.y+10.0, height*.6))
        val shape_type = Random.int(0, 3)
        val shape: ShapeContour = when (shape_type) {
          0 ->
          {
            Circle(center_pt, Random.double(0.0, 0.75 * (center_pt.y - CENTER_OF_IMAGE.y))).contour
          }
          1 -> {
            Rectangle.fromCenter(
              center_pt, Random.double(0.0, 0.75 * (center_pt.y - CENTER_OF_IMAGE.y))
            ).contour
          }
          else -> {
            LineSegment(
              Vector2(Random.double(CENTER_OF_IMAGE.x-.25*width, CENTER_OF_IMAGE.x+.25*width), Random.double(CENTER_OF_IMAGE.y+10.0, height*1.0)),
              Vector2(Random.double(CENTER_OF_IMAGE.x-.25*width, CENTER_OF_IMAGE.x+.25*width), Random.double(CENTER_OF_IMAGE.y+10.0, height*1.0))
            ).contour
          }
        }
        shape
      })

      contours.forEach {
        val numAttractors = Random.int(50, 400)
        it.equidistantPositions(numAttractors).forEach {
          theTree.addAttractor(Attractor(it))
        }
      }

      val Y_MARGIN_MIN = 25.0
      val Y_MARGIN_MAX = height - 150.0
      rootContours.forEach {
        val numAttractors = Random.int(10, 100)
        it.equidistantPositions(numAttractors).forEach { it ->
          // Check if we are within the margins
          if (it.y > Y_MARGIN_MIN && it.y < Y_MARGIN_MAX) {
            theRoots.addAttractor(Attractor(it))
          }
        }
      }

      theTree.addRootNode(Node(CENTER_OF_IMAGE, color = palette.random()))
      theRoots.addRootNode(Node(CENTER_OF_IMAGE, color = palette.random()))

    }

    stateManager.resetHandler = ::reset
    stateManager.reset()
    // Take a timestamped screenshot with the space bar
    extend(camera)
    extend(recorder) {
      profile = MP4Profile()
    }

    extend {
      drawer.clear(palette.background)
      if (!stateManager.isPaused) {
        theTree.step()
        theRoots.step()
      }

      theTree.draw(drawer)
      theRoots.draw(drawer)
      if (stateManager.isDebug) {
        drawer.isolated {
          drawer.stroke = palette.colors[0].opacify(.5)
          drawer.fill = ColorRGBa.TRANSPARENT
          drawer.contours(contours)
          drawer.contours(rootContours)

        }
      }

      // If no more nodes have been added then this iteration is done
      if (!theTree.nodesAdded && !theRoots.nodesAdded && !stateManager.isIterationComplete) {
        recorder.shutdown(program)

        println("Saving compositions")
        var composition = drawComposition {
          fill = null
          theTree.drawComposition(this)
          theRoots.drawComposition(this)
          rectangle(0.0,0.0, width.toDouble(), height.toDouble())
        }

        composition.dedupe().saveToFile(File("/home/cwoodall/Desktop/${seed}-${stateManager.iterationCount}.svg"))
        recorder = ScreenRecorder()
        extend(recorder) {
          profile = MP4Profile()
        }
        recorder.setup(program)


      }

      stateManager.isIterationComplete = !theTree.nodesAdded && !theRoots.nodesAdded

      stateManager.postUpdate(camera)
    }

    // Setup the picture for presentation mode which will go to the next
    // iteration on button press
    mouse.buttonUp.listen {
      stateManager.reset()
      window.requestDraw()
    }

    // Setup listener events for turning on and off debug mode or pausing
    //   d -> toggle debug mode
    //   p -> toggle paused
    keyboard.keyUp.listen {
      if (it.name == "d") {
        stateManager.isDebug = !stateManager.isDebug
      } else if (it.name == "p") {
        stateManager.isPaused = !stateManager.isPaused
      }
    }

  }
}
