package util

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.shape.CompositionStrokeWeight

enum class DrawingStyle {
  OUTLINE_ONLY,
  FILL_OUTLINE_OFF,
  FILL_OUTLINE_ON
}

fun setDrawingStyle(drawer: Drawer, drawingStyle: DrawingStyle, color: ColorRGBa, strokeWeight: Double = 3.0) {
  when (drawingStyle) {
    DrawingStyle.OUTLINE_ONLY -> {
      drawer.fill = ColorRGBa.TRANSPARENT
      drawer.strokeWeight = strokeWeight
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

