package io.github.kamilperczynski.javalint.cli.commands

import io.github.kamilperczynski.javalint.formatter.IntellijFormatter
import io.github.kamilperczynski.javalint.formatter.codestyle.JavaLintCodeStyle
import java.nio.file.Path
import java.util.concurrent.Callable
import kotlin.math.min

class CheckFormattingCommand(
  private val paths: List<Path>,
  private val codeStyle: JavaLintCodeStyle,
  private val projectPath: Path,
  private val formatterEvents: CheckFormattingCommandEvents
) : Callable<Int> {

  override fun call(): Int {
    val formatter = IntellijFormatter(projectPath, formatterEvents)

    formatterEvents.formattingStarted()

    for (path in paths) {
      formatter.formatFile(path, codeStyle) { _, _ -> }
    }

    formatterEvents.formattingEnd()

    return min(formatterEvents.reformattedFilesCount, 1)
  }

}
