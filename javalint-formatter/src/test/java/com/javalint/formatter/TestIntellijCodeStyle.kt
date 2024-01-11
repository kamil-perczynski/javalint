package com.javalint.formatter

import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.IndentOptions
import com.javalint.codestyle.JavaLintCodeStyle
import java.nio.file.Path

class TestIntellijCodeStyle(private val indentOptions: IndentOptions) : JavaLintCodeStyle {

  override fun configure(file: Path, settings: CodeStyleSettings): CodeStyleSettings {
    settings.indentOptions.INDENT_SIZE = indentOptions.INDENT_SIZE
    settings.indentOptions.CONTINUATION_INDENT_SIZE = indentOptions.CONTINUATION_INDENT_SIZE

    return settings
  }

}
