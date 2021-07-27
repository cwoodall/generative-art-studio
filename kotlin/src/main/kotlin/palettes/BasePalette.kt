package palettes

import org.openrndr.color.ColorRGBa

open class BasePalette() {
  open fun random(): ColorRGBa { return ColorRGBa.WHITE }
  open fun background() : ColorRGBa { return ColorRGBa.BLACK }
}
