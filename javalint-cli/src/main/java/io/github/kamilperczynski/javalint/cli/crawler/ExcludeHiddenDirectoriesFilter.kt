package io.github.kamilperczynski.javalint.cli.crawler

import java.nio.file.Path
import kotlin.io.path.isHidden

class ExcludeHiddenDirectoriesFilter(private val excludedDirs: List<String>) : PathsFilter {

  override fun matchDir(dir: Path): Boolean {
    val dirName = dir.fileName.toString()

    if (dir.isHidden()) {
      return false
    }

    if (excludedDirs.contains(dirName)) {
      return false
    }

    return true
  }

  override fun matchFile(path: Path): Boolean {
    return !path.isHidden()
  }
}
