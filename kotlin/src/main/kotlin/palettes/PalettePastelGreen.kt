package palettes

import org.openrndr.color.ColorRGBa

// Taken from https://www.schemecolor.com/twilight.php
class PalettePastelGreen : BasePalette() {
  override val colors = listOf(
    ColorRGBa.fromHex("D7F2BA"),
    ColorRGBa.fromHex("BDE4A8"),
    ColorRGBa.fromHex("9CC69B"),
    ColorRGBa.fromHex("79B4A9"),
    ColorRGBa.fromHex("676F54"),
  )

  override val background = ColorRGBa.fromHex("FFEECF")
}
