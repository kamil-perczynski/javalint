package com.javalint.gitignore

import junit.framework.TestCase
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths

class GitignorePatternTest : TestCase() {

  fun testName() {

    listOf<Path>(
      Paths.get("target/augias-1.0.0-SNAPSHOT.jar"),
      Paths.get("target/augias-1.0.0-SNAPSHOT-fat.jar"),


      )


    val pathMatcher = FileSystems.getDefault().getPathMatcher("glob:.idea/**/workspace.xml")

    val matches = pathMatcher.matches(Paths.get(".idea/workspace.xml"))

    println(matches)
    assert(matches)

  }
}
