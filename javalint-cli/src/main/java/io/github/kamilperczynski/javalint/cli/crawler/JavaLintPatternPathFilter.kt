package io.github.kamilperczynski.javalint.cli.crawler

import java.nio.file.Path
import kotlin.io.path.isHidden
import kotlin.io.path.name
import kotlin.io.path.relativeTo

class JavaLintPatternPathFilter(
  private val projectRoot: Path,
  private val patterns: JavaLintPathPatterns,
  private val excludedDirs: List<String> = listOf("target", "build", "dist", "node_modules"),
) : PathsFilter {

  override fun matchDir(dir: Path): Boolean {
    if (projectRoot == dir) {
      return true
    }

    val normalizedPath = dir.relativeTo(projectRoot).normalize()

    // Glob path matching in java does not work correctly for directories
    // For globs e.g. target/** to work it is required to match
    // against an artificial file.
    // see: GlobJavaLintPathPatternTest#testMatchDirectoryName
    val matchedPattern = patterns.matches(normalizedPath.resolve("artificial-file"))

    if (dir.isHidden() || excludedDirs.contains(dir.name)) {
      return isExplicitlyIncludingPattern(matchedPattern)
    }

    if (matchedPattern == null) {
      return true
    }

    return matchedPattern.type == JavaLintPathPattern.Type.INCLUDES
  }

  override fun matchFile(path: Path): Boolean {
    val normalizedPath = path.relativeTo(projectRoot).normalize()

    val pathPattern = patterns.matches(normalizedPath) ?: return true

    return pathPattern.type == JavaLintPathPattern.Type.INCLUDES
  }

  private fun isExplicitlyIncludingPattern(matchedPattern: JavaLintPathPattern?) =
    matchedPattern?.type == JavaLintPathPattern.Type.INCLUDES

}
