package com.javalint.cli.commands

import com.javalint.codestyle.JavaLintCodeStyle
import com.javalint.formatter.IntellijFormatter
import com.javalint.formatter.IntellijFormatterOptions
import com.javalint.formatter.output.CheckFormattingCommandEvents
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

    formatter.formatFiles(paths, codeStyle) { _, _ -> }

    return min(formatterEvents.reformattedFilesCount, 1)
  }
}
