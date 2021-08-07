package util

import org.openrndr.extensions.Screenshots

class DrawingStateManager {
  var max_iterations = 0
  var is_paused = false
  var wait_frames = 0
  var is_debug = true
  var is_complete = false // Is the shape drawing complete?
  var iterations = 0
  var reset_fn: (() -> Unit)? = null

  fun reset() {
    iterations = 0
    is_complete = false
    wait_frames = 0
    reset_fn?.invoke()
  }

  fun postUpdate(camera: Screenshots? = null) {
    // If we are complete and paused, don't do anything
    // If we are complete and not paused take a screenshot, this involves waiting for a frame or two
    // before resetting the drawing
    if (is_complete) {
      if (!is_paused) {
        if (wait_frames == 0) {
          camera?.trigger()
          wait_frames += 1
        } else if (wait_frames >= 5) {
          if ((iterations <= 0) || !(iterations >= (max_iterations - 1))) {
            iterations++
            if (is_debug) {
              println("Iteration $iterations")
            }
            reset_fn?.invoke()
            wait_frames = 0
            is_complete = false
          }
        } else {
          wait_frames += 1
        }
      }
    }
  }
}
