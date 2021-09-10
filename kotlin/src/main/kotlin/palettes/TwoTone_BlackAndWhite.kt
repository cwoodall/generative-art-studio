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

class BlackBG_White : BasePalette() {
  override val colors = listOf(
    ColorRGBa.WHITE,
  )

  override val background = ColorRGBa.BLACK

  override fun background(): ColorRGBa {
    return background
  }

  override fun random(): ColorRGBa {
    return colors.random()
  }
}

class WhiteBG_Black : BasePalette() {
  override val colors = listOf(
    ColorRGBa.BLACK,
  )

  override val background = ColorRGBa.WHITE

  override fun background(): ColorRGBa {
    return background
  }

  override fun random(): ColorRGBa {
    return colors.random()
  }
}
