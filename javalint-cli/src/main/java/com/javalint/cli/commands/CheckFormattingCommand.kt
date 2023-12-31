package com.javalint.cli.commands

import com.javalint.codestyle.InlineJavaLintCodeStyle
import com.javalint.formatter.IntellijFormatter
import com.javalint.formatter.IntellijFormatterOptions
import com.javalint.formatter.output.CheckFormattingCommandEvents
import com.javalint.gitignore.ExcludedHiddenDirectoriesFilter
import com.javalint.gitignore.discoverProjectFiles
import java.nio.file.Path
import java.util.concurrent.Callable
import kotlin.math.min

class CheckFormattingCommand(private val projectRoot: Path) : Callable<Int> {
  override fun call(): Int {
    val formatterEvents = CheckFormattingCommandEvents(projectRoot)

    val formatter = IntellijFormatter(
      IntellijFormatterOptions(projectRoot, formatterEvents)
    )

    val paths = discoverProjectFiles(
      projectRoot,
      ExcludedHiddenDirectoriesFilter(listOf("target", "build", "dist"))
    )

    formatter.formatFiles(paths, InlineJavaLintCodeStyle()) { _, _ -> }

    return min(formatterEvents.reformattedFilesCount, 1)
  }
}
