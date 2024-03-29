package io.github.kamilperczynski.javalint.formatter

import java.nio.file.Path

interface FormatterEvents {

  val reformattedFilesCount: Int

  fun formattingStarted()
  fun fileFormattingStarted(path: Path)
  fun fileIgnored(path: Path)
  fun fileFormattingEnd(path: Path, isModified: Boolean)
  fun formattingEnd()

}
