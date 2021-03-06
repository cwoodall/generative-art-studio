package palettes

import org.openrndr.color.ColorRGBa

// Taken from https://www.schemecolor.com/twilight.php
class PaletteTwilight : BasePalette() {
  override val colors = listOf(
    ColorRGBa.fromHex("314D7F"),
    ColorRGBa.fromHex("4E7093"),
    ColorRGBa.fromHex("B58D7A"),
    ColorRGBa.fromHex("9B6F6E"),
    ColorRGBa.fromHex("70526E"),
    ColorRGBa.fromHex("594465"),
  )

  override val background = ColorRGBa.fromHex("FFEECF")
}
