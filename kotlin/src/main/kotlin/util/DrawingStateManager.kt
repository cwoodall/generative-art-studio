package util

import org.openrndr.extensions.Screenshots

class DrawingStateManager {
  var maxIterations = 0
  var isPaused = false
  var waitFrameCount = 0
  var isDebug = true
  private var isComplete = false
    set(value) {
      if (value && (value != field)) onCompletionHandler?.invoke()
      field = value
    }
  var isIterationComplete = false // Is the shape drawing complete?
    set(value) {
      if (value && (value != field)) iterationStateChangeHandler?.invoke()
      field = value
    }

  var iterationCount = 0
  var resetHandler: (() -> Unit)? = null
  var iterationStateChangeHandler: (() -> Unit)? = null
  var onCompletionHandler: (() -> Unit)? = null
  fun reset() {
    iterationCount = 0
    isIterationComplete = false
    waitFrameCount = 0
    isComplete = false
    resetHandler?.invoke()
  }

  fun postUpdate(camera: Screenshots? = null) {
    // If we are complete and paused, don't do anything
    // If we are complete and not paused take a screenshot, this involves waiting for a frame or two
    // before resetting the drawing
    if (isIterationComplete) {
      if (!isPaused) {
        if (waitFrameCount == 0) {
          camera?.trigger()
          waitFrameCount += 1
        } else if (waitFrameCount >= 5) {
          if ((maxIterations <= 0) || (iterationCount < (maxIterations - 1))) {
            iterationCount++
            if (isDebug) {
              println("Iteration $iterationCount")
            }
            resetHandler?.invoke()
            waitFrameCount = 0
            isIterationComplete = false
          } else {
            if (isComplete == false) {
              isComplete = true
              println("Complete")
            }
          }
        } else {
          waitFrameCount += 1
        }
      }
    }
  }
}
