package com.javalint.gitignore

import java.nio.file.FileVisitResult
import java.nio.file.Path

interface PathsFilter {

  fun matchDir(dir: Path): FileVisitResult

  fun matchFile(path: Path): FileVisitResult

}
