package com.javalint.ec.settings

import com.intellij.psi.codeStyle.CodeStyleDefaults
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.IndentOptions
import com.javalint.codestyle.JavaLintCodeStyle
import com.javalint.logging.Slf4j
import java.nio.file.Path

class ECCodeStyle(private val propertiesSource: ECSource) : JavaLintCodeStyle {

  companion object : Slf4j()

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
        "charset" -> logUnsupportedProperty(property)
        "indent_style" -> logUnsupportedProperty(property)
        "trim_trailing_whitespace" -> logUnsupportedProperty(property)
        "insert_final_newline" -> logUnsupportedProperty(property)

        "indent_size" -> {
          settings.indentOptions.INDENT_SIZE = property.rawValue.toInt()
          settings.indentOptions.CONTINUATION_INDENT_SIZE = property.rawValue.toInt()
        }

        "ij_continuation_indent_size" ->
          if (isDefaultContinuationIndentSize(settings.indentOptions)) {
            settings.indentOptions.CONTINUATION_INDENT_SIZE = property.rawValue.toInt()
          }

        // TODO: parse into allowed values by ec4j/intellij
        "end_of_line" -> settings.LINE_SEPARATOR = property.rawValue
        "max_line_length" -> settings.defaultRightMargin = property.rawValue.toInt()
        "tab_width" -> settings.indentOptions.TAB_SIZE = property.rawValue.toInt()
        else -> codeStyleSettingsAdapter.setIjProperty(property)
      }
    }

    return settings
  }

  private fun logUnsupportedProperty(property: ECProperty) {
    log.debug("Unsupported property: {}", property.name)
  }

}

private fun isDefaultContinuationIndentSize(indentOptions: IndentOptions) =
  indentOptions.CONTINUATION_INDENT_SIZE == CodeStyleDefaults.DEFAULT_CONTINUATION_INDENT_SIZE
