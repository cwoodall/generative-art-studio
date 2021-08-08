package util

import org.openrndr.extensions.Screenshots

class DrawingStateManager {
  var max_iterations = 0
  var is_paused = false
  var wait_frames = 0
  var isDebug = true
  var isComplete = false // Is the shape drawing complete?
  var iterations = 0
  var reset_fn: (() -> Unit)? = null

  fun reset() {
    iterations = 0
    isComplete = false
    wait_frames = 0
    reset_fn?.invoke()
  }

  fun postUpdate(camera: Screenshots? = null) {
    // If we are complete and paused, don't do anything
    // If we are complete and not paused take a screenshot, this involves waiting for a frame or two
    // before resetting the drawing
    if (isComplete) {
      if (!is_paused) {
        if (wait_frames == 0) {
          camera?.trigger()
          wait_frames += 1
        } else if (wait_frames >= 5) {
          if ((iterations <= 0) || !(iterations >= (max_iterations - 1))) {
            iterations++
            if (isDebug) {
              println("Iteration $iterations")
            }
            reset_fn?.invoke()
            wait_frames = 0
            isComplete = false
          }
        } else {
          wait_frames += 1
        }
      }
    }
  }
}
