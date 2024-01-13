package io.github.kamilperczynski.javalint.cli.crawler

import io.github.kamilperczynski.javalint.cli.crawler.JavaLintPathPattern.*
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher

data class GlobJavaLintPathPattern(
  override val text: String,
  override val type: Type,
  private val matcher: PathMatcher,
) : JavaLintPathPattern {

  override fun matches(path: Path) = matcher.matches(path)

}

fun parseCliJavaLintPathPattern(rawLine: String): JavaLintPathPattern {
  val patternType =
    if (rawLine.startsWith("!")) Type.IGNORES else Type.INCLUDES

  val line = if (rawLine[0] == '!') rawLine.substring(1) else rawLine

  val globPattern = "glob:$line"
  val pathMatcher = FileSystems.getDefault().getPathMatcher(globPattern)

  return GlobJavaLintPathPattern(globPattern, patternType, pathMatcher)
}
