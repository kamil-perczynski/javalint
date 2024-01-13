package io.github.kamilperczynski.javalint

import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.zip.ZipInputStream

fun extractZipArchive(resourcePath: String, targetDir: Path): Int {
  var filesCount = 0

  ZipInputStream(MainKtTest::class.java.classLoader.getResourceAsStream(resourcePath)!!).use {
    var zipEntry = it.nextEntry
    val buffer = ByteArray(1024)

    while (zipEntry != null) {
      if (!zipEntry.isDirectory) {
        val filePath = targetDir.resolve(Paths.get(zipEntry.name))

        if (!Files.exists(filePath.parent)) {
          Files.createDirectories(filePath.parent)
        }

        Files.newOutputStream(filePath, StandardOpenOption.CREATE).use { fos ->
          var len: Int = it.read(buffer)
          while (len > 0) {
            fos.write(buffer, 0, len)
            len = it.read(buffer)
          }
          fos.close()
          filesCount += 1
        }

      }

      zipEntry = it.nextEntry
    }

    it.closeEntry()

  }

  return filesCount
}


fun deleteExtractedDir(baseDir: Path) {
  val fileVisitor = object : FileVisitor<Path> {
    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult {
      return FileVisitResult.CONTINUE
    }

    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
      Files.delete(file)
      return FileVisitResult.CONTINUE
    }

    override fun visitFileFailed(file: Path, exc: IOException?): FileVisitResult {
      return FileVisitResult.CONTINUE
    }

    override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
      Files.delete(dir)
      return FileVisitResult.CONTINUE
    }


  }
  Files.walkFileTree(baseDir, fileVisitor)
}
