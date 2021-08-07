package sketch

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.draw.rectangleBatch
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.Random
import org.openrndr.ffmpeg.MP4Profile
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.LineSegment
import org.openrndr.shape.Rectangle
import org.openrndr.shape.contour
import org.w3c.dom.Attr
import palettes.BasePalette
import palettes.PalettePastelGreen
import palettes.PaletteTwilight
import palettes.Palette_00
import util.DrawingStateManager
import util.Quadtree

//import org.openrndr.extra.

class Attractor(position: Vector2, influenceRadius: Double = 500.0, killRadius: Double = 10.0) {
  val position = position
  val influenceRadius = influenceRadius
  val killRadius = killRadius

  fun draw(drawer: Drawer, fill: ColorRGBa = ColorRGBa.BLUE, radius: Double = 1.0) {
    drawer.isolated {
      drawer.stroke = fill
      drawer.fill = fill
      drawer.circle(position, radius)
    }
  }
}

class Node(position: Vector2, color: ColorRGBa = ColorRGBa.BLACK) {
  val position: Vector2 = position
  var averageDirection: Vector2 = Vector2.ZERO
  var color: ColorRGBa = color
  var drawInluenceDirectionLength: Double = 0.0
  var isDrawInfluenceDirection: Boolean  = true
  fun draw(drawer: Drawer, radius: Double = 4.0) {
    drawer.isolated {
      drawer.stroke = color
      drawer.fill = color
      drawer.circle(position, radius)
      if (isDrawInfluenceDirection) drawer.lineSegment(position, (position+averageDirection*drawInluenceDirectionLength))
    }

  }

  fun attractorInfluence(attractor: Vector2) {
    averageDirection += (attractor - position).normalized
    averageDirection = averageDirection.normalized
  }

  fun reset() {
    averageDirection = Vector2.ZERO
  }
}

class World(bounds: Rectangle, maxObjects: Int = 10, segmentLength: Double = 1.0) {
  var attractors: MutableSet<Attractor> = mutableSetOf()
  var nodes: MutableSet<Node> = mutableSetOf()
  var qtree: Quadtree<Node> = Quadtree(bounds, maxObjects) { it.position }
  val segmentLength = segmentLength
  var count = 0
  val loneRadius = segmentLength * 3.0
  // What is a better way to handle this collision radius?
  val collisionRadius = segmentLength * .9 // Radius for detecting collisions between nodes
  var nodesAdded: Boolean = true

  fun addAttractor(attractor: Attractor) {
    attractors.add(attractor)
    nodesAdded = true
  }

  fun buildSpatialIndex() {
    qtree.clear()
    nodes.forEach { qtree.insert(it) }
  }

  fun findNearestNode(pos: Vector2, radius: Double): Node? {
    val nearest = qtree.nearest(Node(pos), radius) ?: return null
    return nearest.nearest
  }

  fun findNearestNeighbors(pos: Vector2, radius: Double): List<Node>? {
    val nearest = qtree.nearest(Node(pos), radius) ?: return null
    return nearest.neighbours
  }

  fun step(drawer: Drawer) {
    nodesAdded = false
    nodes.forEach { it.reset() }

    buildSpatialIndex()

    for (attractor in attractors) {
      val nearest = findNearestNode(attractor.position, attractor.influenceRadius)
      nearest?.attractorInfluence(attractor.position)
    }


    // Add new nodes
    var new_nodes = mutableListOf<Node>()
    for (node in nodes) {
      if (node.averageDirection != Vector2.ZERO) {
        val n = Node(node.position + node.averageDirection * segmentLength, color = node.color)
        val nearest = findNearestNode(n.position, collisionRadius)
        if (nearest == null) {
          new_nodes.add(n)
          nodesAdded = true
        }
      }
    }
    nodes.addAll(new_nodes)

    // Remove all attractors that have a node within their kill radius
    var kill_attractors = mutableListOf<Attractor>()
    for (attractor in attractors) {
      val nearestNodes = findNearestNode(attractor.position, attractor.killRadius)
      if (nearestNodes != null) {
        kill_attractors.add(attractor)
        nodesAdded = true
      }
    }
    attractors.removeAll(kill_attractors)

    // Remove all lone nodes
    if (count == 2) {
      var loneNodes: MutableSet<Node> = mutableSetOf()
      // Remove lone nodes
      for (node in nodes) {
        val nearestNode = findNearestNeighbors(node.position, loneRadius)
        if (nearestNode == null || nearestNode?.size == 1) {
          loneNodes.add(node)
          nodesAdded = true
        }
      }
      nodes.removeAll(loneNodes)
    }
    count++
  }


  fun draw(drawer: Drawer) {
    for (node in nodes) {
        node.drawInluenceDirectionLength = segmentLength
        node.draw(drawer)
    }
  }
}

fun main(args: Array<String>) = application {
  // Setup argument parsing
  val parser = ArgParser("sketch")
  val width_arg by parser.option(ArgType.Int, fullName = "width", shortName = "w", description = "width (px)")
    .default(1000)
  val height_arg by parser.option(ArgType.Int, fullName = "height", shortName = "e", description = "height (px)")
    .default(1000)
  val seed by parser.option(ArgType.Int, shortName = "s", description = "seed").default(1230)
  val _max_iterations by parser.option(ArgType.Int, shortName = "n", description = "Number of iterations").default(-1)

  parser.parse(args)

  configure {
    width = width_arg // Width of picture
    height = height_arg // Height of picture
  }

  program {
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

    val centerOfImage = Vector2(width*.5, height*.5)
    var theWorld = World(Rectangle.fromCenter(centerOfImage, width * .5, height * .5))


    val palettes: Array<BasePalette> = arrayOf(
      PalettePastelGreen(),
      Palette_00(),
      PaletteTwilight()
    )
    var palette: BasePalette = Palette_00()

    /**
     * Internal function for what we do to reset the drawing, this means regenerating new contours and arcs
     */
    fun reset() {
      palette = palettes.random()
      theWorld = World(Rectangle.fromCenter(centerOfImage, width.toDouble(), height.toDouble()))
      val number_shapes = Random.int(1,20)
      for (i in 0 until number_shapes) {
        val shape_type = Random.int(0, 3)
        val shape = when (shape_type) {
          0 -> Circle(centerOfImage + Random.vector2(-100.0, 100.0), Random.double(100.0, width.toDouble())).contour
          1 -> Rectangle.fromCenter(centerOfImage + Random.vector2(-100.0, 100.0), Random.double(100.0, width.toDouble())).contour
          else -> LineSegment(Random.vector2(0.0, width * 1.0),Random.vector2(0.0, width * 1.0)).contour
        }
        shape.equidistantPositions(Random.int(10,400)).forEach {
          theWorld.addAttractor(Attractor(it))
        }
      }

      val num_seed_nodes = Random.int(1,20)
      for (i in 0 until num_seed_nodes) {
        theWorld.nodes.add(Node(Random.vector2(0.0, width * 1.0), color=palette.random()))
      }
    }

    state_manager.reset_fn = ::reset
    state_manager.max_iterations = 100
    state_manager.reset()
    // Take a timestamped screenshot with the space bar
    var camera = Screenshots()
    extend(camera)
    var recorder = ScreenRecorder()
    extend(recorder) {
      profile = MP4Profile()
    }
    extend {
      theWorld.step(drawer)

      drawer.clear(palette.background)
      theWorld.draw(drawer)

//      state_manager.is_complete = !theWorld.nodesAdded
      state_manager.postUpdate(camera)
    }
  }
}
