package io.github.kamilperczynski.javalint.cli.gitignore

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher

data class GlobJavaLintPathPattern(
  override val text: String,
  private val matcher: PathMatcher,
  override val type: JavaLintPathPattern.Type
) : JavaLintPathPattern {

  override fun matches(path: Path) = matcher.matches(path)

}

fun parseCliJavaLintPathPattern(rawLine: String): JavaLintPathPattern {
  val patternType =
    if (rawLine.startsWith("!")) JavaLintPathPattern.Type.IGNORES else JavaLintPathPattern.Type.INCLUDES

  val line = if (rawLine[0] == '!') rawLine.substring(1) else rawLine

  val globPattern = "glob:$line"
  val pathMatcher = FileSystems.getDefault().getPathMatcher(globPattern)

  return GlobJavaLintPathPattern(globPattern, pathMatcher, patternType)
}
