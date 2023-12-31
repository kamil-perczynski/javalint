package com.javalint.gitignore

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Files
import java.nio.file.Path

data class GitignorePatterns(val patterns: List<GitignorePattern>) {

  companion object {
    val log: Logger = LogManager.getLogger(this::class.java.declaringClass.javaClass)
  }

  fun isIncluded(path: Path): Boolean {
    val matchedPattern = patterns.stream()
      .filter { pattern -> pattern.matcher.matches(path) }
      .findFirst()

    val isIncluded = matchedPattern
      .map { it.type == GitignorePattern.Type.INCLUDES }
      .orElse(true)

    if (!isIncluded) {
      val msg = matchedPattern.orElse(null)?.globPattern ?: "<missing>"
      log.debug("{} ignores {}", msg, path)
    }

    return isIncluded
  }

}

fun parseGitignoreFile(rootPath: Path): GitignorePatterns {
  val allPatterns = Files.readAllLines(rootPath.resolve(".gitignore"))
    .asSequence()
    .map(String::trim)
    .filter(String::isNotEmpty)
    .filter { !it.startsWith('#') }
    .map(::parseGitIgnorePattern)
    .sortedWith(GitignorePatternComparator.INSTANCE)
    .toList()

  return GitignorePatterns(allPatterns)
}

