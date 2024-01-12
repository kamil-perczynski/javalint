package io.github.kamilperczynski.javalint.cli.commands

import io.github.kamilperczynski.javalint.formatter.IntellijFormatter
import io.github.kamilperczynski.javalint.formatter.IntellijFormatterOptions
import io.github.kamilperczynski.javalint.formatter.codestyle.JavaLintCodeStyle
import io.github.kamilperczynski.javalint.formatter.output.CheckFormattingCommandEvents
import java.nio.file.Path
import java.util.concurrent.Callable
import kotlin.math.min

class CheckFormattingCommand(
  private val projectRoot: Path,
  private val paths: List<Path>,
  private val codeStyle: JavaLintCodeStyle
) : Callable<Int> {

  override fun call(): Int {
    val formatterEvents = CheckFormattingCommandEvents(projectRoot)

    val formatter = IntellijFormatter(
      IntellijFormatterOptions(projectRoot, formatterEvents)
    )

    formatterEvents.formattingStarted()

    for (path in paths) {
      formatter.formatFile(path, codeStyle) { _, _ -> }
    }

    formatterEvents.formattingEnd()

    return min(formatterEvents.reformattedFilesCount, 1)
  }

}
