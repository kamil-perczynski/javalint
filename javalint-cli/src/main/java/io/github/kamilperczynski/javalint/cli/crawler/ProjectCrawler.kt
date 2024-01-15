package io.github.kamilperczynski.javalint.cli.crawler

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.relativeTo

fun discoverProjectFiles(
  projectRoot: Path,
  pathsFilter: PathsFilter,
  maxFiles: Int = Int.MAX_VALUE
): List<Path> {
  val paths = mutableListOf<Path>()

  val fileVisitor = object : FileVisitor<Path> {

    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult {
      return if (pathsFilter.matchDir(dir))
        FileVisitResult.CONTINUE
      else
        FileVisitResult.SKIP_SUBTREE
    }

    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
      if (pathsFilter.matchFile(file)) {
        val relativeFile = file.relativeTo(projectRoot)
        paths.add(relativeFile)

        if (paths.size > maxFiles) {
          throw IllegalStateException("Files crawled limit reached ($maxFiles). Increase the limit with --max-files option")
        }
      }

      return FileVisitResult.CONTINUE
    }

    override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult =
      FileVisitResult.CONTINUE

    override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult =
      FileVisitResult.CONTINUE

  }

  Files.walkFileTree(projectRoot, fileVisitor)

  return paths
}
