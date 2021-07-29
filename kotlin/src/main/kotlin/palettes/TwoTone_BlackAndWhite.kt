package palettes

import org.openrndr.color.ColorRGBa

// Taken from https://www.schemecolor.com/twilight.php
class TwoTone_BlackAndWhite : BasePalette() {
  override val colors = listOf(
    ColorRGBa.BLACK,
    ColorRGBa.WHITE,
    ColorRGBa.GRAY,
  )

  override val background = ColorRGBa.fromHex("FFEECF")

  override fun background(): ColorRGBa {
    return background
  }
  override fun random(): ColorRGBa {
    return colors.random()
  }
}
