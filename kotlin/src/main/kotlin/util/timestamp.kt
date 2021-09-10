package util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun timestamp(time: LocalDateTime = LocalDateTime.now()): String {
  val currentTime = LocalDateTime.now()
  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss")
  return currentTime.format(dateTimeFormatter)
}
