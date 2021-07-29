package palettes

import org.openrndr.color.ColorRGBa

open class BasePalette() {
  open val colors: List<ColorRGBa> = listOf()
  open val background = ColorRGBa.BLACK

  open fun random(): ColorRGBa { return ColorRGBa.WHITE }
  open fun background() : ColorRGBa { return background }

  // Getter with a wrap
  open fun wrappedGet(x: Int): ColorRGBa {
    return colors[x % colors.size]
  }

  open fun get(x: Int): ColorRGBa {
    return colors[x]
  }
}
