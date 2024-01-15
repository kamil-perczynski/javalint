package io.github.kamilperczynski.javalint.cli.crawler

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class JavaLintPatternPathFilterTest {

  @Test
  fun testFilterOutDefaultExcludedDirectories() {
    // given:
    val paths = listOf(
      Paths.get("src"),
      Paths.get("docs"),
      Paths.get("target"),
      Paths.get("build"),
      Paths.get(".mvn"),
    )

    // when:
    val pathFilter = JavaLintPatternPathFilter(
      Paths.get("."),
      JavaLintPathPatterns(listOf())
    )

    // then:
    val matchedPaths = paths.stream()
      .filter { pathFilter.matchDir(it) }
      .sorted()
      .toList()

    Assertions.assertEquals(Paths.get("docs"), matchedPaths[0])
    Assertions.assertEquals(Paths.get("src"), matchedPaths[1])
    Assertions.assertEquals(2, matchedPaths.size)
  }

  @Test
  fun testIncludeDirectoriesGlobs() {
    // given:
    val paths = listOf(
      Paths.get("src"),
      Paths.get("docs"),
      Paths.get("target"),
      Paths.get("build"),
      Paths.get(".mvn"),
    )

    val pathFilter = JavaLintPatternPathFilter(
      Paths.get("."),
      JavaLintPathPatterns(
        listOf(
          parseCliJavaLintPathPattern("target/**"),
          parseCliJavaLintPathPattern(".mvn/**"),
          parseCliJavaLintPathPattern("!src/**")
        )
      )
    )

    // when:
    val matchedPaths = paths.stream()
      .filter { pathFilter.matchDir(it) }
      .sorted()
      .toList()

    // then:
    Assertions.assertEquals(Paths.get(".mvn"), matchedPaths[0])
    Assertions.assertEquals(Paths.get("docs"), matchedPaths[1])
    Assertions.assertEquals(Paths.get("target"), matchedPaths[2])
    Assertions.assertEquals(3, matchedPaths.size)
  }

}
