package io.github.kamilperczynski.javalint.cli.commands

import io.github.kamilperczynski.javalint.formatter.FormatterEvents
import java.nio.file.Path

class FixFormattingCommandEvents(private val homePath: Path) : FormatterEvents {

  private var formattingStartedAt: Long = 0

  private var filesWithCorrectStyleCount = 0
  override var reformattedFilesCount = 0

  override fun formattingStarted() {
    print(ConsoleColor.WHITE_BOLD_BRIGHT)
    print("Formatting all files at: $homePath")
    print(ConsoleColor.RESET)
    println()
  }

  override fun fileFormattingStarted(path: Path) {
    formattingStartedAt = System.currentTimeMillis()
  }

  override fun fileIgnored(path: Path) {
    print(ConsoleColor.BLACK_BRIGHT)
    print(path.toString().padEnd(110))
    print("".padStart(10))
    print(" (ignored)".padStart(10))
    print(ConsoleColor.RESET)
    println()
  }

  override fun fileFormattingEnd(path: Path, isModified: Boolean) {
    val duration = System.currentTimeMillis() - formattingStartedAt

    if (isModified) {
      reformattedFilesCount++
      print(path.toString().padEnd(110))
      print(" ${duration}ms".padStart(10))
      println()
    } else {
      filesWithCorrectStyleCount++

      print(ConsoleColor.BLACK_BRIGHT)
      print(path.toString().padEnd(110))
      print(" ${duration}ms".padStart(10))
      print(" (unchanged)")
      print(ConsoleColor.RESET)
      println()
    }
  }

  override fun formattingEnd() {
    println()
    println("✅ Reformatted $reformattedFilesCount files, correctly formatted files: $filesWithCorrectStyleCount")
  }

}
