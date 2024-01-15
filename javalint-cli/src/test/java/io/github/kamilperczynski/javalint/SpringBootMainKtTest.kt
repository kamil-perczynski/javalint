package io.github.kamilperczynski.javalint

import io.github.kamilperczynski.javalint.cli.executeCli
import io.github.kamilperczynski.javalint.formatter.logging.Slf4j
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path

class SpringBootMainKtTest {

  companion object : Slf4j()

  private lateinit var systemOut: PrintStream
  private lateinit var baseDir: Path

  private val consoleOutput = ByteArrayOutputStream()

  @BeforeEach
  fun setUp() {
    baseDir = Files.createTempDirectory(javaClass.simpleName).toRealPath()

    extractZipArchive("spring-boot-sample.zip", baseDir)
    log.info("Extracted spring-boot-sample.zip to {}", baseDir)

    systemOut = System.out
    System.setOut(PrintStream(consoleOutput))
  }

  @AfterEach
  fun tearDown() {
    System.setOut(systemOut)
    log.info("Deleting {}", baseDir)
//    deleteExtractedDir(baseDir)

    println(consoleOutput())
  }

  @Test
  fun testCheckFormattingWithCustomPatterns() {
    // given:
    val args = arrayOf(".mvn/**", "src/**", "!target", "--cwd", baseDir.toString())

    // when:
    val exitCode = executeCli(*args)


    // then:
    val output = consoleOutput()

    assertTrue(output.contains(Regex("Checking files at: [a-zA-Z_\\-0-9/]+ against the code style")))
    assertTrue(output.contains(Regex("src/test/java/com/example/demo/DemoApplicationTests.java \\s+ \\d+ms")))
    assertTrue(output.contains(Regex("src/main/resources/application.yaml \\s+ \\d+ms")))
    assertTrue(output.contains("Found 2 files with incorrect formatting"))

    assertEquals(1, exitCode)
  }

  @Test
  fun testCheckFormattingWithCustomEditorconfig() {
    // given:
    Files.writeString(
      baseDir.resolve("src/main/resources/application.yaml"),
      """
        server :
          port : 8081

      """.trimIndent()
    )

    val customEditorConfig = Files.createTempFile(this::class.java.simpleName, ".tmp")
    Files.writeString(
      customEditorConfig,
      """
        root = true
        indent_size = 4

        [*.yaml]
        ij_yaml_indent_size = 2
        ij_yaml_space_before_colon = true
      """.trimIndent()
    )

    // when:
    val args = arrayOf(
      "!src/test/**",
      "--editorconfig",
      customEditorConfig.toRealPath().toString(),
      "--cwd",
      baseDir.toString()
    )

    val exitCode = executeCli(*args)

    // then:
    assertEquals(0, exitCode)
  }

  @Test
  fun testFixAndCheckFormattingWithDefaultCodeStyle() {
    val exitCode1 =
      executeCli(".mvn/**", "src/**", "!target", "--cwd", baseDir.toString())
    val exitCode2 =
      executeCli(".mvn/**", "src/**", "!target", "--cwd", baseDir.toString(), "-F")
    val exitCode3 =
      executeCli(".mvn/**", "src/**", "!target", "--cwd", baseDir.toString())

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
    // given:
    val args = arrayOf("-F", "--cwd", baseDir.toString())

    // when:
    val exitCode = executeCli(*args)

    // then:
    val output = consoleOutput()
    assertTrue(output.contains(Regex("Formatting all files at: [a-zA-Z_\\-0-9/]+")))
    assertTrue(output.contains(Regex("mvnw\\.cmd \\s+ \\(ignored\\)")))
    assertTrue(output.contains(Regex("pom\\.xml \\s+ \\d+ms \\(unchanged\\)")))
    assertTrue(output.contains(Regex("HELP\\.md \\s+ \\(ignored\\)")))
    assertTrue(output.contains(Regex("mvnw \\s+ \\(ignored\\)")))
    assertTrue(output.contains(Regex("src/test/java/com/example/demo/DemoApplicationTests.java \\s+ \\d+ms")))
    assertTrue(output.contains(Regex("src/main/resources/application.yaml \\s+ \\d+ms")))
    assertTrue(output.contains("Reformatted 2 files, correctly formatted files: 2"))

    assertEquals(0, exitCode)
  }

  @Test
  fun testPrintVersion() {
    // given & when:
    val exitCode = executeCli("--version")

    // then:
    assertEquals(0, exitCode)

    val lines = consoleOutput().lines()
    assertEquals("0.0-SNAPSHOT.TEST", lines[0])
  }

  @Test
  fun testPrintUsageInfo() {
    val exitCode = executeCli("--help")
    assertEquals(0, exitCode)
  }

  private fun consoleOutput() = String(consoleOutput.toByteArray())

}
