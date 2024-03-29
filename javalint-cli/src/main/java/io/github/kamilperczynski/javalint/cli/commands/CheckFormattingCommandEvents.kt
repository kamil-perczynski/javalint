package io.github.kamilperczynski.javalint.cli.commands

import io.github.kamilperczynski.javalint.formatter.FormatterEvents
import java.nio.file.Path

class CheckFormattingCommandEvents(
  private val homePath: Path,
  private val maxErrors: Int
) : FormatterEvents {

  private var formattingStartedAt: Long = 0

  override var reformattedFilesCount = 0

  override fun formattingStarted() {
    print(ConsoleColor.WHITE_BOLD_BRIGHT)
    print("Checking files at: $homePath against the code style")
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
    print('\r')
  }

  override fun fileFormattingEnd(path: Path, isModified: Boolean) {
    val duration = System.currentTimeMillis() - formattingStartedAt

    if (isModified) {
      reformattedFilesCount++
      print(path.toString().padEnd(110))
      print(" ${duration}ms".padStart(10))
      print("            ")
      println()

    } else {
      print(ConsoleColor.BLACK_BRIGHT)
      print(path.toString().padEnd(110))
      print(" ${duration}ms".padStart(10))
      print(" (unchanged)")
      print(ConsoleColor.RESET)
      print('\r')
    }

    if (reformattedFilesCount > maxErrors) {
      throw IllegalStateException("Formatting errors limit reached ($maxErrors). Increase the limit with --limit option")
    }
  }

  override fun formattingEnd() {
    println(" ".repeat(132))

    if (reformattedFilesCount > 0) {
      println("⛔ Found $reformattedFilesCount files with incorrect formatting")
    } else {
      println("✅ All files matched the code style")
    }
  }

}
