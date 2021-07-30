package util

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer

enum class DrawingStyle {
  OUTLINE_ONLY,
  FILL_OUTLINE_OFF,
  FILL_OUTLINE_ON
}

fun setDrawingStyle(drawer: Drawer, drawingStyle: DrawingStyle, color: ColorRGBa) {
  when (drawingStyle) {
    DrawingStyle.OUTLINE_ONLY -> {
      drawer.fill = ColorRGBa.TRANSPARENT
      drawer.strokeWeight = 10.0
      drawer.stroke = color
    }

    DrawingStyle.FILL_OUTLINE_OFF -> {
      drawer.stroke = ColorRGBa.TRANSPARENT
      drawer.fill = color
    }

    DrawingStyle.FILL_OUTLINE_ON -> {
      drawer.stroke = ColorRGBa.BLACK
      drawer.fill = color
    }
  }
}

