package io.github.kamilperczynski.javalint.formatter.ec

import com.intellij.json.JsonLanguage
import com.intellij.lang.java.JavaLanguage
import com.intellij.lang.xml.XMLLanguage
import com.intellij.psi.codeStyle.CodeStyleDefaults
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.IndentOptions
import com.intellij.util.LineSeparator
import io.github.kamilperczynski.javalint.formatter.codestyle.JavaLintCodeStyle
import io.github.kamilperczynski.javalint.formatter.logging.Slf4j
import org.jetbrains.yaml.YAMLLanguage
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class ECCodeStyle(private val propertiesSource: ECSource) : JavaLintCodeStyle {

  companion object : Slf4j()

  override fun charset(file: Path): Charset {
    val ecCharsetValue = propertiesSource.charset(file)

    return toCharset(ecCharsetValue)
  }

  override fun configure(file: Path, settings: CodeStyleSettings): CodeStyleSettings {
    val properties = propertiesSource.findECProps(file)

    return codeStyleSettings(settings, properties)
  }

  private fun codeStyleSettings(
    settings: CodeStyleSettings,
    editorConfigProperties: List<ECProperty>
  ): CodeStyleSettings {
    val codeStyleSettingsAdapter = ECCodeStyleSettingsAdapter(settings)

    for (property in editorConfigProperties) {
      when (property.name) {
        "charset" ->
          log.trace("Charset property is ignored by ECCodeStyle")

        "indent_style" -> {
          settings.indentOptions.USE_TAB_CHARACTER = toUseTabs(property.rawValue)
          executeForAllCommonSettings(settings) {
            it.indentOptions!!.USE_TAB_CHARACTER = toUseTabs(property.rawValue)
          }
        }


        "tab_width" -> {
          settings.indentOptions.TAB_SIZE = property.rawValue.toInt()
          executeForAllCommonSettings(settings) {
            it.indentOptions!!.TAB_SIZE = property.rawValue.toInt()
          }
        }


        "trim_trailing_whitespace" ->
          log.trace("Trailing whitespace is obligatory trimmed by IJ")

        "insert_final_newline" ->
          log.trace("insert_final_newline is not supported by IJ. https://youtrack.jetbrains.com/issue/IDEA-320289")

        "indent_size" -> {
          if (!isNumber(property.rawValue)) {
            break
          }

          settings.indentOptions.INDENT_SIZE = property.rawValue.toInt()
          settings.indentOptions.CONTINUATION_INDENT_SIZE = property.rawValue.toInt()

          executeForAllCommonSettings(settings) {
            it.indentOptions!!.INDENT_SIZE = property.rawValue.toInt()
            it.indentOptions!!.CONTINUATION_INDENT_SIZE = property.rawValue.toInt()
          }
        }

        "ij_continuation_indent_size" -> {
          if (!isDefaultContinuationIndentSize(settings.indentOptions)) {
            break
          }

          settings.indentOptions.CONTINUATION_INDENT_SIZE = property.rawValue.toInt()
          executeForAllCommonSettings(settings) {
            it.indentOptions!!.CONTINUATION_INDENT_SIZE = property.rawValue.toInt()
          }
        }

        "end_of_line" -> {
          log.debug("EC property end_of_line is not supported in IJ 2023.3. https://youtrack.jetbrains.com/issue/IDEA-285800")
          settings.LINE_SEPARATOR = toLineSeparator(property.rawValue)
        }

        "max_line_length" ->
          settings.defaultRightMargin = property.rawValue.toInt()

        else -> codeStyleSettingsAdapter.setIjProperty(property)
      }
    }

    return settings
  }

  private fun executeForAllCommonSettings(
    rootSettings: CodeStyleSettings,
    fn: (commonSettings: CommonCodeStyleSettings) -> Unit
  ) {
    fn.invoke(rootSettings.getCommonSettings(JavaLanguage.INSTANCE))
    fn.invoke(rootSettings.getCommonSettings(XMLLanguage.INSTANCE))
    fn.invoke(rootSettings.getCommonSettings(JsonLanguage.INSTANCE))
    fn.invoke(rootSettings.getCommonSettings(YAMLLanguage.INSTANCE))
  }

}

private fun isNumber(rawValue: String): Boolean {
  return rawValue.chars().allMatch { Character.isDigit(it) }
}

private fun isDefaultContinuationIndentSize(indentOptions: IndentOptions) =
  indentOptions.CONTINUATION_INDENT_SIZE == CodeStyleDefaults.DEFAULT_CONTINUATION_INDENT_SIZE

private fun toCharset(value: String): Charset {
  return when (value) {
    "utf-8" -> StandardCharsets.UTF_8
    "utf-8-bom" -> StandardCharsets.UTF_8
    "utf-16be" -> StandardCharsets.UTF_16
    "utf-16le" -> StandardCharsets.UTF_16
    "latin1" -> StandardCharsets.ISO_8859_1
    else -> throw IllegalArgumentException("Illegal charset value: $value")
  }
}

private fun toUseTabs(value: String): Boolean {
  return when (value) {
    "tab" -> true
    "space" -> false
    else -> throw IllegalArgumentException("Illegal indent_style value: $value")
  }
}

private fun toLineSeparator(value: String): String {
  return when (value) {
    "lf" -> LineSeparator.LF.separatorString
    "crlf" -> LineSeparator.CRLF.separatorString
    "cr" -> LineSeparator.CR.separatorString
    else -> throw IllegalArgumentException("Illegal end_of_line value: $value")
  }
}
