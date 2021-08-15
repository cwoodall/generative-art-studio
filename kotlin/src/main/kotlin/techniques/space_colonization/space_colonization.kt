package techniques.space_colonization

import kotlinx.coroutines.yield
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.DrawQuality
import org.openrndr.draw.Drawer
import org.openrndr.draw.LineCap
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import org.openrndr.math.clamp
import org.openrndr.shape.*
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
  private var attractors: MutableList<Attractor> = mutableListOf()
  private var nodes: MutableList<Node> = mutableListOf()
  private var rootNodes: MutableList<Node> = mutableListOf()
  var qtree: Quadtree<Node> = Quadtree(bounds, maxObjects) { it.position }
  val segmentLength = segmentLength

  // What is a better way to handle this collision radius?
  val collisionRadius = segmentLength * .9 // Radius for detecting collisions between nodes
  var nodesAdded: Boolean = true
  var minRadius = 3.0
  var maxRadius = 15.0
  var growthRate = growthRate

  fun reset() {
    qtree.clear()
    attractors.clear()
    nodes.clear()
    rootNodes.clear()
    nodesAdded = true
  }

  fun addAttractor(attractor: Attractor) {
    attractors.add(attractor)
    nodesAdded = true
  }

  fun addRootNode(node: Node) {
    nodes.add(node)
    rootNodes.add(node)
  }

  fun addRootNodes(nodes: List<Node>) = this.nodes.addAll(nodes)
  fun rootNodes(): List<Node> = rootNodes.toList()

  private fun buildSpatialIndex() {
    qtree.clear()
    nodes.forEach { qtree.insert(it) }
  }

  private fun findNearestNode(pos: Vector2, radius: Double): Node? {
    val nearest = qtree.nearest(Node(pos), radius) ?: return null
    return nearest.nearest
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
    nodes.removeAll(rootNodes.filter { it.children.size == 0 })
  }

  fun draw(drawer: Drawer) {
    val nodes_by_color = nodes.groupBy { it.color }
    for ((color, nodes) in nodes_by_color) {
      drawer.stroke = color
      drawer.fill = color
      drawer.circles(nodes.map {
        val radius = (maxRadius * (it.numDescendants() / it.numChildren()) * growthRate).clamp(minRadius, maxRadius)
        it.toCircle(radius)
      })
    }
  }

  @OptIn(ExperimentalStdlibApi::class)
  fun drawPath(drawer: Drawer) {
    fun addChildrenToContour(contourBuilder: ContourBuilder, node: Node) {
      if (node.children.size > 0) {
        // Add nodes in a depth first manner
        for (child in node.children.sortedBy { node.numDescendants() }.reversed()) {
          contourBuilder.moveOrLineTo(node.position)
          addChildrenToContour(contourBuilder, child)
        }
        // Once a branch is exhausted  move back to the root node to create a new contour
        contourBuilder.moveTo(node.position)
      }
    }

    rootNodes.forEach {
      val c = contours {
        moveTo(it.position)
        addChildrenToContour(this, it)
      }

      drawer.stroke = it.color
      drawer.strokeWeight = 3.0
      drawer.fill = ColorRGBa.TRANSPARENT
      drawer.drawStyle.quality = DrawQuality.QUALITY
      drawer.lineCap = LineCap.ROUND

      drawer.contours(c)
    }
  }

  // TODO(cw) the compositions still seem to close themselves
  fun drawComposition(drawer: CompositionDrawer) {
    fun addChildrenToContour(contourBuilder: ContourBuilder, node: Node) {
      if (node.children.size > 0) {
        // Add nodes in a depth first manner
        for (child in node.children.sortedBy { node.numDescendants() }.reversed()) {
          contourBuilder.lineTo(node.position)
          addChildrenToContour(contourBuilder, child)
        }
        // Once a branch is exhausted  move back to the root node to create a new contour
        contourBuilder.moveTo(node.position)
      }
    }

    rootNodes.forEach {
      val c = contours {
        moveTo(it.position)
        addChildrenToContour(this, it)
      }

      drawer.stroke = it.color
      drawer.strokeWeight = 10.0
      drawer.fill = ColorRGBa.TRANSPARENT
      drawer.contours(c)
    }
  }
}
