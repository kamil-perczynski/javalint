package com.javalint.ec4j.linter.settings

import com.intellij.psi.codeStyle.CodeStyleDefaults
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.IndentOptions
import com.javalint.codestyle.JavaLintCodeStyle
import com.javalint.ec4j.linter.logging.Slf4j

class EditorConfigJavaLintCodeStyle(private val properties: List<EditorConfigProperty>) :
  JavaLintCodeStyle {

  companion object : Slf4j()

  override fun configure(settings: CodeStyleSettings): CodeStyleSettings {
    val codeStyleSettingsAdapter = EditorConfigCodeStyleSettingsAdapter(settings)

    for (property in properties) {
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

  private fun logUnsupportedProperty(property: EditorConfigProperty) {
    log.debug("Unsupported property: {}", property.name)
  }

}

private fun isDefaultContinuationIndentSize(indentOptions: IndentOptions) =
  indentOptions.CONTINUATION_INDENT_SIZE == CodeStyleDefaults.DEFAULT_CONTINUATION_INDENT_SIZE
