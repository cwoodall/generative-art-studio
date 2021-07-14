package palettes

import org.openrndr.color.ColorRGBa


class Palette_00() : BasePalette() {
  val colors = listOf(
    ColorRGBa.fromHex("FCB264"), // orange
    ColorRGBa.fromHex("8DC0E2"), // blue
    ColorRGBa.fromHex("C4B2E1"), // purple
    ColorRGBa.fromHex("CEE4F2"), // light blue
    ColorRGBa.fromHex("FEE1C3"), // light orange
    ColorRGBa.fromHex("E9E2F4"), // light purple
  )

  override fun random(): ColorRGBa {
    return colors.random()
  }
}
