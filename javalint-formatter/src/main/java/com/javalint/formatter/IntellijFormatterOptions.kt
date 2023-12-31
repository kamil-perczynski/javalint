package com.javalint.formatter

import java.nio.file.Path

data class IntellijFormatterOptions(
  val homePath: Path,
  val formatterEvents: FormatterEvents
)
