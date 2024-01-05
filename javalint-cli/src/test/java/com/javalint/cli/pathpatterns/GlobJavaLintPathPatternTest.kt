package com.javalint.cli.pathpatterns

import com.javalint.cli.gitignore.JavaLintPathPattern
import com.javalint.cli.gitignore.parseCliJavaLintPathPattern
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class GlobJavaLintPathPatternTest {

  @Test
  fun testMatchExcludingPattern() {
    val pattern = parseCliJavaLintPathPattern("!target/**")

    val matches = pattern.matches(
      Paths.get("target/classes/Boo.class")
    )

    assertEquals(JavaLintPathPattern.Type.IGNORES, pattern.type)
    assertEquals(true, matches)
  }

  @Test
  fun testMatchGlobFilePattern() {
    val pattern = parseCliJavaLintPathPattern("target/**/*.{java,class}")

    val matches = pattern.matches(
      Paths.get("target/classes/Boo.class")
    )

    assertEquals(JavaLintPathPattern.Type.INCLUDES, pattern.type)
    assertEquals(true, matches)
  }

  @Test
  fun testMatchDirectoryName() {
    val pattern = parseCliJavaLintPathPattern(".mvn/**")

    // Paths#get transforms path into ".mvn" which does not match
    // with pattern regex "^\.mvn/.*"
    val matches = pattern.matches(
      Paths.get(".mvn/")
    )

    assertEquals(JavaLintPathPattern.Type.INCLUDES, pattern.type)
    assertEquals(false, matches)
  }

}
