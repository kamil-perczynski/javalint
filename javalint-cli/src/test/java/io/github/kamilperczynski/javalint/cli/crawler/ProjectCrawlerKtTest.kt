package io.github.kamilperczynski.javalint.cli.crawler

import io.github.kamilperczynski.javalint.deleteExtractedDir
import io.github.kamilperczynski.javalint.extractZipArchive
import io.github.kamilperczynski.javalint.formatter.logging.Slf4j
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.nio.file.Files
import java.nio.file.Path

class ProjectCrawlerKtTest {

  companion object : Slf4j()

  private lateinit var baseDir: Path

  @BeforeEach
  fun setUp() {
    baseDir = Files.createTempDirectory(javaClass.simpleName).toRealPath()

    extractZipArchive("spring-boot-sample.zip", baseDir)
    log.info("Extracted spring-boot-sample.zip to {}", baseDir)

  }

  @AfterEach
  fun tearDown() {
    log.info("Deleting {}", baseDir)
    deleteExtractedDir(baseDir)
  }

  @Test
  fun testDiscoverAllProjectFiles() {
    // given:
    val discoverProjectFiles = discoverProjectFiles(
      baseDir,
      JavaLintPatternPathFilter(baseDir, JavaLintPathPatterns(emptyList()))
    )

    // when:
    val listing = discoverProjectFiles
      .sorted()
      .joinToString("\n")

    // then:
    assertEquals(
      """
        HELP.md
        mvnw
        mvnw.cmd
        pom.xml
        src/main/java/com/example/demo/DemoApplication.java
        src/main/resources/application.yaml
        src/test/java/com/example/demo/DemoApplicationTests.java
      """.trimIndent(),
      listing
    )
  }

  @Test
  fun testReachLimitOfDiscoveredFiles() {
    // given:
    val call = Executable {
      discoverProjectFiles(
        baseDir,
        JavaLintPatternPathFilter(baseDir, JavaLintPathPatterns(emptyList())),
        5
      )
    }

    // when & then:
    assertThrows(IllegalStateException::class.java, call)
  }

}
