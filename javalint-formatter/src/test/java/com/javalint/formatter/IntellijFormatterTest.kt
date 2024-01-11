package com.javalint.formatter

import com.intellij.psi.codeStyle.CommonCodeStyleSettings.IndentOptions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE
import kotlin.text.Charsets.UTF_8

class IntellijFormatterTest {

  companion object {
    private lateinit var baseDir: Path
    private lateinit var formatter: IntellijFormatter

    @JvmStatic
    @BeforeAll
    fun setUp() {
      baseDir = Files.createTempDirectory("IntellijFormatterTest");

      formatter = IntellijFormatter(
        IntellijFormatterOptions(baseDir, TestFormatterEvents.INSTANCE)
      )
    }
  }

  @Test
  fun testFormatJava() {
    // given:
    val outfile = baseDir.resolve("BlobsController.java")
    val source = readSnapshot("samples/BlobsController.java.sample")

    Files.write(outfile, source.toByteArray(UTF_8), CREATE)

    val codeStyle = TestIntellijCodeStyle(toIndentOptions())

    // when & then:
    formatter.formatFile(outfile, codeStyle) { _, el ->
      fail(String(el.textToCharArray()))
    }
  }

  @Test
  fun testFixFormattingJava() {
    // given:
    val source = readSnapshot("samples/ExitNodeHelper.java.sample")
    val expectedResult = readSnapshot("snapshots/ExitNodeHelper.java.snapshot")

    val outfile = baseDir.resolve("ExitNodeHelper.java")
    Files.write(outfile, source.toByteArray(UTF_8), CREATE)

    val javaLintCodeStyle = TestIntellijCodeStyle(toIndentOptions())

    // when & then:
    formatter.formatFile(outfile, javaLintCodeStyle) { _, el ->
      val result = String(el.textToCharArray())

      assertEquals(expectedResult, result)
    }
  }

  @Test
  fun testFormatXml() {
    // given:
    val source = readSnapshot("samples/logback.xml.sample")

    val outfile = baseDir.resolve("logback.xml")
    Files.write(outfile, source.toByteArray(UTF_8), CREATE)

    val codeStyle = TestIntellijCodeStyle(IndentOptions())

    // when & then:
    formatter.formatFile(outfile, codeStyle) { _, el ->
      fail(String(el.textToCharArray()))
    }
  }

  @Test
  fun testFormatUnknownFile() {
    // given:
    val source = readSnapshot("samples/mvnw.sample")

    val outfile = baseDir.resolve("mvnw")
    Files.write(outfile, source.toByteArray(UTF_8), CREATE)

    val codeStyle = TestIntellijCodeStyle(IndentOptions())

    // when & then:
    formatter.formatFile(outfile, codeStyle) { _, el ->
      fail(String(el.textToCharArray()))
    }
  }

}

private fun toIndentOptions(): IndentOptions {
  val indentOptions = IndentOptions()
  indentOptions.INDENT_SIZE = 2
  indentOptions.CONTINUATION_INDENT_SIZE = 2
  return indentOptions
}

private fun readSnapshot(resourcePath: String) = String(
  IntellijFormatterTest::class.java.classLoader
    .getResourceAsStream(resourcePath)!!
    .readAllBytes()
)
