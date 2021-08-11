package sketch.g02

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.DrawStyle
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.Random
import org.openrndr.ffmpeg.MP4Profile
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.Vector2
import org.openrndr.math.clamp
import org.openrndr.shape.*
import palettes.BasePalette
import palettes.PalettePastelGreen
import palettes.PaletteTwilight
import palettes.Palette_00
import util.DrawingStateManager
import util.Quadtree

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

class Node(
  position: Vector2,
  color: ColorRGBa = ColorRGBa.BLACK,
  parent: Node? = null,
  children: MutableSet<Node> = mutableSetOf()
) {
  var parent: Node? = parent
  var children: MutableSet<Node> = children
  val position: Vector2 = position
  var averageDirection: Vector2 = Vector2.ZERO
  var color: ColorRGBa = color
  var drawInluenceDirectionLength: Double = 0.0
  var isDrawInfluenceDirection: Boolean = true

  fun toCircle(radius: Double = 2.0) = Circle(position, radius)

  fun addChild(child: Node) {
    if (child != this) {
      children.add(child)
      child.parent = this
    }
  }

  fun numChildren(): Double = children.size.toDouble()
  fun numDescendants(): Double = children.map { it.numDescendants() }.sum() + numChildren()

  fun draw(drawer: Drawer, radius: Double = 2.0) {
    drawer.isolated {
      drawer.stroke = color
      drawer.fill = color
      drawer.circle(toCircle())
      if (isDrawInfluenceDirection) drawer.lineSegment(
        position,
        (position + averageDirection * drawInluenceDirectionLength)
      )
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

class Network(bounds: Rectangle, maxObjects: Int = 30, segmentLength: Double = 2.0, growthRate: Double = 0.0025) {
  var attractors: MutableSet<Attractor> = mutableSetOf()
  var nodes: MutableSet<Node> = mutableSetOf()
  var qtree: Quadtree<Node> = Quadtree(bounds, maxObjects) { it.position }
  val segmentLength = segmentLength
  var count = 0
  val loneRadius = segmentLength * 3.0

  // What is a better way to handle this collision radius?
  val collisionRadius = segmentLength * .9 // Radius for detecting collisions between nodes
  var nodesAdded: Boolean = true
  var nodesCircleRadius = 4.0
  var minRadius = 2.0
  var maxRadius = 15.0
  var growthRate = growthRate

  fun reset() {
    qtree.clear()
    attractors.clear()
    nodes.clear()
    nodesAdded = true
  }

  fun addAttractor(attractor: Attractor) {
    attractors.add(attractor)
    nodesAdded = true
  }

  fun addRootNode(node :Node) = nodes.add(node)
  fun addRootNode(nodes :List<Node>) = this.nodes.addAll(nodes)
  fun rootNodes(): List<Node> = nodes.filter { it.parent == null }

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
          node.addChild(n)
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
      nodes.removeAll(rootNodes().filter { it.children.size == 0 })
    }
    count++
  }

  fun draw(drawer: Drawer) {
    val nodes_by_color = nodes.groupBy { it.color }
    for ((color, nodes) in nodes_by_color) {
      drawer.stroke = color
      drawer.fill = color
      drawer.circles(nodes.map {
        val radius = (maxRadius *(it.numDescendants()/it.numChildren())  * growthRate).clamp(minRadius, maxRadius)
        it.toCircle(radius)
      })
    }
  }

  fun drawPath(drawer: Drawer) {
    // Change this to create a contour of the whole tree then draw it in one batch
    fun drawChildrenPath(drawer: Drawer, node: Node) {
      if (node.children.size > 0) {
        for (child in node.children) {
          drawer.strokeWeight = (maxRadius *(node.numDescendants()/node.numChildren())  * growthRate).clamp(minRadius, maxRadius)
          drawer.stroke = node.color
          drawer.fill = node.color
          drawer.lineSegment(node.position, child.position)
          drawChildrenPath(drawer, child)
        }
      }
    }
    rootNodes().forEach { drawChildrenPath(drawer, it) }
  }
}

fun main(args: Array<String>) = application {
  // Setup argument parsing
  val parser = ArgParser("sketch")
  val width_arg by parser.option(ArgType.Int, fullName = "width", shortName = "w", description = "width (px)")
    .default(1000)
  val height_arg by parser.option(ArgType.Int, fullName = "height", shortName = "e", description = "height (px)")
    .default(1000)
  // 9432
  val seed by parser.option(ArgType.Int, shortName = "s", description = "seed").default(1223)
  val _max_iterations by parser.option(ArgType.Int, shortName = "n", description = "Number of iterationCount").default(0)
  val monoline by parser.option(ArgType.Boolean, shortName = "m", description = "Grow at a rate or force monoline").default(false)
  val quitOnCompletion by parser.option(ArgType.Boolean, shortName = "q", description = "Quite when completed wiht given iterations").default(false)

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
      growthRate=growthRate)


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
        theWorld.step(drawer)
      }

      if (stateManager.isDebug) {
        drawer.isolated {
          drawer.stroke = palette.colors[0].opacify(.5)
          drawer.fill = ColorRGBa.TRANSPARENT
          drawer.contours(contours)
        }
      }
      theWorld.drawPath(drawer)

      // If no more nodes have been added then this iteration is done
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
