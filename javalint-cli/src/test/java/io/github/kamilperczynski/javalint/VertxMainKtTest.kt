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
import kotlin.text.Charsets.UTF_8

val editorconfigContent = """
  root = true

  [*]
  charset = utf-8
  indent_style = space
  indent_size = 2

  trim_trailing_whitespace = true
  end_of_line = lf
  insert_final_newline = true
  max_line_length = 100

  [*.java]
  ij_any_method_call_chain_wrap = split_into_lines
  ij_any_wrap_first_method_in_call_chain = true
  ij_any_call_parameters_wrap = split_into_lines
  ij_any_call_parameters_new_line_after_left_paren = true
  ij_any_call_parameters_right_paren_on_new_line = true

  ij_any_parentheses_expression_right_paren_on_new_line = true
  ij_any_parentheses_expression_new_line_after_left_paren = true

  ij_java_method_parameters_wrap = split_into_lines
  ij_java_prefer_parameters_wrap = true

""".trimIndent()

class VertxMainKtTest {

  companion object : Slf4j()

  private lateinit var systemOut: PrintStream
  private lateinit var baseDir: Path

  private val consoleOutput = ByteArrayOutputStream()

  @BeforeEach
  fun setUp() {
    baseDir = Files.createTempDirectory(javaClass.simpleName).toRealPath()

    extractZipArchive("vertx-starter.zip", baseDir)
    log.info("Extracted vertx-starter.zip to {}", baseDir)

    systemOut = System.out
    System.setOut(PrintStream(consoleOutput))
  }

  @AfterEach
  fun tearDown() {
    System.setOut(systemOut)
    log.info("Deleting {}", baseDir)
    deleteExtractedDir(baseDir)

    println(consoleOutput())
  }

  @Test
  fun testFixFormatting() {
    // given:
    Files.writeString(baseDir.resolve(".editorconfig"), editorconfigContent, UTF_8)

    val args = arrayOf("-F", "--cwd", baseDir.toString())

    // when:
    val exitCode = executeCli(*args)

    // then:
    val output = consoleOutput()
    assertTrue(output.contains(Regex("src/test/java/com/example/starter/TestMainVerticle.java \\s+ \\d+ms\n")))
    assertTrue(output.contains(Regex("src/main/java/com/example/starter/MainVerticle.java \\s+ \\d+ms\n")))
    assertTrue(output.contains(Regex("pom.xml \\s+ \\d+ms\n")))
    assertTrue(output.contains(Regex("mvnw.cmd \\s+ \\(ignored\\)")))
    assertTrue(output.contains("Reformatted 3 files, correctly formatted files: 0"))

    assertEquals(0, exitCode)

    val expectedContent = readSnapshot("MainVerticle.java.sample")
    val reformattedFileSrc = Files.readString(
      baseDir.resolve("src/main/java/com/example/starter/MainVerticle.java")
    )

    assertEquals(expectedContent, reformattedFileSrc)
  }

  private fun readSnapshot(resourcePath: String) = String(
    javaClass.classLoader
      .getResourceAsStream(resourcePath)!!
      .readAllBytes()
  )

  private fun consoleOutput(): String {
    return String(consoleOutput.toByteArray())
  }

}
