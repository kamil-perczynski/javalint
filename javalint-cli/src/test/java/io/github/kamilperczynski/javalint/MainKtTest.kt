package io.github.kamilperczynski.javalint

import io.github.kamilperczynski.javalint.cli.executeCli
import io.github.kamilperczynski.javalint.formatter.logging.Slf4j
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path


class MainKtTest {

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
  fun testCheckFormattingWithCustomPatterns() {
    val exitCode = executeCli(".mvn/**", "src/**", "!target", "--cwd", baseDir.toString())

    assertEquals(1, exitCode)
  }

  @Test
  fun testFixAndCheckFormattingWithDefaultCodeStyle() {
    val exitCode1 = executeCli(".mvn/**", "src/**", "!target", "--cwd", baseDir.toString())
    val exitCode2 = executeCli(".mvn/**", "src/**", "!target", "--cwd", baseDir.toString(), "-F")
    val exitCode3 = executeCli(".mvn/**", "src/**", "!target", "--cwd", baseDir.toString())

    assertEquals(1, exitCode1)
    assertEquals(0, exitCode2)
    assertEquals(0, exitCode3)
  }

  @Test
  fun testCheckFormattingWithDefaultSettings() {
    val exitCode = executeCli("--cwd", baseDir.toString())
    assertEquals(1, exitCode)
  }

  @Test
  fun testFixFormatting() {
    val exitCode = executeCli("-F", "--cwd", baseDir.toString())
    assertEquals(0, exitCode)
  }

  @Test
  fun testPrintVersion() {
    val exitCode = executeCli("--version")
    assertEquals(0, exitCode)
  }

  @Test
  fun testPrintUsageInfo() {
    val exitCode = executeCli("--help")
    assertEquals(0, exitCode)
  }

}
