package palettes

import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.Random
import org.openrndr.math.Vector4

open class BasePalette {
  open val colors: List<ColorRGBa> = listOf(ColorRGBa.WHITE)
  open val background = ColorRGBa.BLACK

  open fun random(): ColorRGBa = colors[Random.int0(colors.lastIndex)]
  open fun background(): ColorRGBa = background

  // Getter with a wrap
  open fun wrappedGet(x: Int): ColorRGBa = colors[x % colors.size]

  open fun get(x: Int): ColorRGBa = colors[x]

  open fun getHSV(x: Int): ColorHSVa = colors[x].toHSVa()

  open fun wrappedGetHSV(x: Int): ColorHSVa = wrappedGet(x).toHSVa()

  open fun toArray(): Array<ColorRGBa> = colors.toTypedArray()
  open fun toVector4Array(): Array<Vector4> {
    return colors.map { it.toVector4() }.toTypedArray()
  }
}
