package sketch.g02

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.Random
import org.openrndr.ffmpeg.MP4Profile
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.Vector2
import org.openrndr.math.clamp
import org.openrndr.shape.*
import org.openrndr.svg.saveToFile
import palettes.BasePalette
import palettes.PalettePastelGreen
import palettes.PaletteTwilight
import palettes.Palette_00
import util.DrawingStateManager

import techniques.space_colonization.*
import java.io.File

fun main(args: Array<String>) = application {
  // Setup argument parsing
  val parser = ArgParser("sketch")
  val width_arg by parser.option(ArgType.Int, fullName = "width", shortName = "w", description = "width (px)")
    .default(1000)
  val height_arg by parser.option(ArgType.Int, fullName = "height", shortName = "e", description = "height (px)")
    .default(1000)
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
    val centerOfImage = Vector2(width * .5, height * .5)
    val growthRate = if (monoline) 0.0 else 0.0025

    var theWorld: Network = Network(
      Rectangle.fromCenter(centerOfImage, width.toDouble(), height.toDouble()),
      growthRate = growthRate
    )


    val palettes: Array<BasePalette> = arrayOf(
      PalettePastelGreen(),
      Palette_00(),
      PaletteTwilight()
    )
    var palette: BasePalette = Palette_00()

    var recorder = ScreenRecorder()
    recorder.maximumDuration = 60.0
    var camera = Screenshots()
    var contours = listOf<ShapeContour>()

    /**
     * Internal function for what we do to reset the drawing, this means regenerating new contours and arcs
     */
    fun reset() {
      palette = palettes[Random.int0(palettes.lastIndex)]
      theWorld.reset()
      val number_shapes = Random.int(1, 20)
      contours = (0 until number_shapes).map {
        val shape_type = Random.int(0, 3)
        val shape: ShapeContour = when (shape_type) {
          0 -> Circle(centerOfImage + Random.vector2(-100.0, 100.0), Random.double(100.0, width.toDouble())).contour
          1 -> Rectangle.fromCenter(
            centerOfImage + Random.vector2(-100.0, 100.0),
            Random.double(100.0, width.toDouble())
          ).contour
          else -> LineSegment(Random.vector2(0.0, width * 1.0), Random.vector2(0.0, width * 1.0)).contour
        }
        shape
      }

      contours.forEach {
        val numAttractors = Random.int(10, 400)
        it.equidistantPositions(numAttractors).forEach {
          theWorld.addAttractor(Attractor(it))
        }
      }

      val num_seed_nodes = Random.int(1, 20)
      for (i in 0 until num_seed_nodes) {
        theWorld.addRootNode(Node(Random.vector2(0.0, width * 1.0), color = palette.random()))
      }
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
        theWorld.step()
      }

      theWorld.draw(drawer)
      if (stateManager.isDebug) {
        drawer.isolated {
          drawer.stroke = palette.colors[0].opacify(.5)
          drawer.fill = ColorRGBa.TRANSPARENT
          drawer.contours(contours)
        }
      }
      // If no more nodes have been added then this iteration is done
      if (!theWorld.nodesAdded && !stateManager.isIterationComplete) {
        println("Saving compositions")
        var composition = drawComposition {
          fill = null
          theWorld.drawComposition(this)
          rectangle(0.0,0.0, width.toDouble(), height.toDouble())
        }

        composition.dedupe().saveToFile(File("/home/cwoodall/Desktop/${seed}-${stateManager.iterationCount}.svg"))
      }
      stateManager.isIterationComplete = !theWorld.nodesAdded

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

///**
// * For a Composition, filter out bezier segments contained in longer bezier segments.
// * The goal is to avoid drawing lines multiple times with a plotter.
// */
//fun Composition.dedupe(err: Double = 1.0): Composition {
//  val segments = this.findShapes().flatMap {
//    it.shape.contours.flatMap { contour -> contour.segments }
//  }
//  val deduped = mutableListOf<Segment>()
//  segments.forEach { curr ->
//    if (deduped.none { other -> other.contains(curr, err) }) {
//      deduped.add(curr)
//    }
//  }
//  return drawComposition {
//    contours(deduped.map { it.contour })
//  }
//}
