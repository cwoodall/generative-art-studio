package util

import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.random
import org.openrndr.math.Vector2
import org.openrndr.shape.ShapeContour
import kotlin.math.min
import kotlin.math.max
import kotlin.math.round

fun clip(x: Double, lower: Double, upper: Double): Double {
  return max(lower, min(upper, x))
}

fun clip(x: Float, lower: Float, upper: Float): Float {
  return max(lower, min(upper, x))
}

fun clip(x: Int, lower: Int, upper: Int): Int {
  return max(lower, min(upper, x))
}

fun clip(x: Long, lower: Long, upper: Long): Long {
  return max(lower, min(upper, x))
}

fun roundToNearestN(x: Double, N: Double): Double {
  return round(x / N) * N
}

fun roundToNearestN(vec: Vector2, N: Double): Vector2 {
  return Vector2(roundToNearestN(vec.x, N), roundToNearestN(vec.y, N))
}

fun randomVector2(min_x: Double, max_x: Double, min_y: Double, max_y: Double): Vector2 {
  return Vector2(Random.double(min_x, max_x), Random.double(min_y, max_y))
}


fun weightedRandomBool(probability: Double): Boolean {
  return Random.double(0.0, 1.0) > (1 - probability)
}


infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
  require(start.isFinite())
  require(endInclusive.isFinite())
  require(step > 0.0) { "Step must be positive, was: $step." }
  val sequence = generateSequence(start) { previous ->
    if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
    val next = previous + step
    if (next > endInclusive) null else next
  }
  return sequence.asIterable()
}

fun ShapeContour.linearSamplePositions(num: Int): List<Vector2> {
  return ((0.0..1.0).step(1.0 / num)).map { this.position(it) }
}

fun <T> getAllUniqueCombinations(x: List<T>, max_size: Int = x.size, min_size: Int = 2): Set<List<T>> {
  fun getAllUniqueCombinationsUpToSize(x: List<T>, n: Int): Set<List<T>> {
    return when (n) {
      0 -> setOf()
      1 -> x.map<T, List<T>> { listOf(it) }.toSet()
      else -> {
        var prev = getAllUniqueCombinationsUpToSize(x, n - 1).toMutableList()
        var ret = mutableSetOf<List<T>>()
        for (i in x) {
          for (p in prev) {
            var list = p.toMutableSet()
            list.add(i)
            ret.add(list.toList())
          }
        }
        ret.addAll(prev)
        ret
      }
    }
  }

  return getAllUniqueCombinationsUpToSize(x, max_size).filter { it.size >= min_size }.sortedBy { it.size }.toSet()
}
