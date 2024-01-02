package com.javalint.cli.gitignore

import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.relativeTo

class JavaLintPatternPathFilter(
  private val projectRoot: Path,
  private val patterns: JavaLintPathPatterns
) : PathsFilter {

  override fun matchDir(dir: Path): Boolean {
    if (projectRoot == dir) {
      return true
    }

    if (dir.name == ".git" || dir.name == "node_modules") {
      return false
    }

    val normalizedPath = dir.relativeTo(projectRoot).normalize()

    // If the directory is not explicitly ignored, then we must include it
    val matchedPattern = patterns.matches(normalizedPath)
      ?: return true

    return matchedPattern.type == JavaLintPathPattern.Type.INCLUDES
  }

  override fun matchFile(path: Path): Boolean {
    val normalizedPath = path.relativeTo(projectRoot).normalize()

    return patterns.matches(normalizedPath)?.type == JavaLintPathPattern.Type.INCLUDES
  }

}
