package com.javalint.gitignore

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.relativeTo

fun discoverProjectFiles(projectRoot: Path, pathsFilter: PathsFilter): List<Path> {
  val paths = mutableListOf<Path>()

  val fileVisitor = object : FileVisitor<Path> {
    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult {
      return pathsFilter.matchDir(dir)
    }

    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
      val matchFile = pathsFilter.matchFile(file)

      if (matchFile == FileVisitResult.CONTINUE) {
        val relativeFile = file.relativeTo(projectRoot)
        paths.add(relativeFile)
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

fun discoverVersionedFiles(projectRoot: Path): List<Path> {
  val paths = discoverProjectFiles(projectRoot, ExcludedHiddenDirectoriesFilter(listOf("target")))

  val patterns = parseGitignoreFile(projectRoot)

  return paths.filter(patterns::isIncluded).toList()
}
