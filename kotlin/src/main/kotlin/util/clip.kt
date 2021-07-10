package util

import kotlin.math.min
import kotlin.math.max

fun clip(x: Double, lower: Double, upper: Double): Double {
  return min(lower, max(upper, x))
}

fun clip(x: Float, lower: Float, upper: Float): Float {
  return min(lower, max(upper, x))
}

fun clip(x: Int, lower: Int, upper: Int): Int {
  return min(lower, max(upper, x))
}

fun clip(x: Long, lower: Long, upper: Long): Long {
  return min(lower, max(upper, x))
}
