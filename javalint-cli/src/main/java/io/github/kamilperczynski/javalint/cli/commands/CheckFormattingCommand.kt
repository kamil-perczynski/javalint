package io.github.kamilperczynski.javalint.cli.commands

import io.github.kamilperczynski.javalint.formatter.IntellijFormatter
import io.github.kamilperczynski.javalint.formatter.IntellijFormatterOptions
import io.github.kamilperczynski.javalint.formatter.codestyle.JavaLintCodeStyle
import java.nio.file.Path
import java.util.concurrent.Callable
import kotlin.math.min

class CheckFormattingCommand(
  private val paths: List<Path>,
  private val codeStyle: JavaLintCodeStyle,
  private val options: IntellijFormatterOptions
) : Callable<Int> {

  override fun call(): Int {
    val formatter = IntellijFormatter(options)

    options.formatterEvents.formattingStarted()

    for (path in paths) {
      formatter.formatFile(path, codeStyle) { _, _ -> }
    }

    options.formatterEvents.formattingEnd()

    return min(options.formatterEvents.reformattedFilesCount, 1)
  }

}
