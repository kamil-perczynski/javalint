package com.javalint.gitignore

import java.nio.file.FileVisitResult
import java.nio.file.Path
import kotlin.io.path.isHidden

class ExcludedHiddenDirectoriesFilter(private val excludedDirs: List<String>) : PathsFilter {

  override fun matchDir(dir: Path): FileVisitResult {
    val dirName = dir.fileName.toString()

    if (dir.isHidden()) {
      return FileVisitResult.SKIP_SUBTREE
    }

    if (excludedDirs.contains(dirName)) {
      return FileVisitResult.SKIP_SUBTREE
    }

    return FileVisitResult.CONTINUE
  }

  override fun matchFile(path: Path): FileVisitResult {
    if (path.isHidden()) {
      return FileVisitResult.SKIP_SUBTREE
    }

    return FileVisitResult.CONTINUE
  }
}
