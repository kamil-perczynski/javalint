package com.javalint.formatter

import java.nio.file.Path

interface FormatterEvents {

  fun formattingStarted()
  fun fileFormattingStarted(path: Path)
  fun fileIgnored(path: Path)
  fun fileFormattingEnd(path: Path, isModified: Boolean)
  fun formattingEnd()

}
