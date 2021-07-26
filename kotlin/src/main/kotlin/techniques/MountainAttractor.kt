package techniques

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.shape.Circle
import util.clip
import kotlin.math.abs
import kotlin.math.pow

abstract class BaseMountainAttractor(width: Int) {
  val width = width
  protected var magnitude_field: List<Double> = (0..width-1).map { 0.0 }
  protected var difference_field: List<Double> = (0..width-1).map { 0.0 }

  open fun maxMagnitude(): Double {
    return magnitude_field.maxOrNull()!!
  }

  open fun get(x: Double): Double {
    return when {
      x < 0 -> difference_field.first()
      x > difference_field.lastIndex -> difference_field.last()
      else -> difference_field[x.toInt()]
    }
  }

  open fun getMagnitude(x: Double): Double {
    return when {
      x < 0 -> magnitude_field.first()
      x > magnitude_field.lastIndex -> magnitude_field.last()
      else -> magnitude_field[x.toInt()]
    }
  }

  protected fun generateDifference(width: Int): List<Double> {
    val magnitudes_future = magnitude_field.subList(1, magnitude_field.lastIndex)
    val magnitudes_past = magnitude_field.subList(0, magnitude_field.lastIndex-1)
    val derivative = magnitudes_past.zip(magnitudes_future).map { (x0, x1) -> x1 - x0}
    return derivative // TODO: How to scale this properly?
  }

  open fun debug_draw(drawer: Drawer, mid_point: Double, scaler: Double, circle_size:Double = 4.0) {
    val circles = (0..magnitude_field.lastIndex).map { x ->
      val magnitude =  magnitude_field[x] * scaler
      Circle(x.toDouble(), mid_point - magnitude, circle_size)
    }
    drawer.circles(circles)
  }
}

class MountainAttractor(center: Double, magnitude: Double, width: Int, K: Double = 5.0, N: Double = 1.0) : BaseMountainAttractor(width) {
  val center = center
  val magnitude = magnitude
  val K: Double = K
  val N: Double = N
  init {
    magnitude_field = generateMagnitude(width)
    difference_field = generateDifference(width)
  }

  private fun generateMagnitude(width: Int): List<Double> {
    return (0..width+1)
      .map { x -> abs(center - x) / width.toDouble() }
      .map { err -> (1.0 - K * err.pow(N))}
      .map { it -> clip(it, 0.0, 1.0) }
      .map { it -> it * magnitude}
  }

  override fun debug_draw(drawer: Drawer, mid_point: Double, scaler: Double, circle_size: Double) {
    if (magnitude > 0) {
      drawer.fill = ColorRGBa.RED
    } else {
      drawer.fill = ColorRGBa.BLUE
    }
    drawer.stroke = ColorRGBa.TRANSPARENT

    super.debug_draw(drawer, mid_point, scaler, circle_size)
  }
}

class SumOfMountainAttractors(width:Int, attractors: List<BaseMountainAttractor>, max_magnitude:Double = 0.0) : BaseMountainAttractor(width) {
  init {
    magnitude_field = (0..width-1).map {
        x -> attractors.map {
          a-> a.getMagnitude(x.toDouble())
        }.sum()
    }

    // rescale if needed
    if (max_magnitude > 0.0) {
      magnitude_field = magnitude_field.map { it ->
        max_magnitude * it / magnitude_field.maxOrNull()!!
      }
    }

    difference_field = generateDifference(width)
  }
}
