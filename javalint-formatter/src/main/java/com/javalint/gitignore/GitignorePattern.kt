package com.javalint.gitignore

import com.javalint.gitignore.GitignorePattern.Type.IGNORES
import com.javalint.gitignore.GitignorePattern.Type.INCLUDES
import java.nio.file.FileSystems
import java.nio.file.PathMatcher

data class GitignorePattern(
  val globPattern: String,
  val matcher: PathMatcher,
  val type: Type
) {

  enum class Type {
    INCLUDES, IGNORES
  }

}

fun parseGitIgnorePattern(line: String): GitignorePattern {
  val isGlobPattern = line.contains('*') || line.contains('?') || line.contains('!')
  val patternType = if (line.startsWith("!")) INCLUDES else IGNORES

  if (isGlobPattern) {
    val globPattern = if (line.startsWith("!")) "glob:${line.substring(1)}" else "glob:$line"
    val pathMatcher = FileSystems.getDefault().getPathMatcher(globPattern)

    return GitignorePattern(globPattern, pathMatcher, patternType)
  }

  if (line[0] == '/') {
    return GitignorePattern("prefix:$line", { path -> path.startsWith(line) }, patternType)
  }

  return GitignorePattern(
    "contains:$line",
    { path -> path.toString().contains(line) },
    patternType
  )
}


