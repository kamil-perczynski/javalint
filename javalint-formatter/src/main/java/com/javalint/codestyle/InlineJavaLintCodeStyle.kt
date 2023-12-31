package com.javalint.codestyle

import com.intellij.lang.java.JavaLanguage
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.JavaCodeStyleSettings

class InlineJavaLintCodeStyle : JavaLintCodeStyle {

  override fun configure(settings: CodeStyleSettings): CodeStyleSettings {
    settings.setRightMargin(null, 110)
    settings.indentOptions.INDENT_SIZE = 2
    settings.indentOptions.CONTINUATION_INDENT_SIZE = 2

    val javaSettings = settings.getCommonSettings(JavaLanguage.INSTANCE)

    javaSettings.CALL_PARAMETERS_WRAP = CommonCodeStyleSettings.WRAP_ON_EVERY_ITEM
    javaSettings.CALL_PARAMETERS_LPAREN_ON_NEXT_LINE = true
    javaSettings.CALL_PARAMETERS_RPAREN_ON_NEXT_LINE = true

    javaSettings.METHOD_CALL_CHAIN_WRAP = CommonCodeStyleSettings.WRAP_ON_EVERY_ITEM
    javaSettings.METHOD_PARAMETERS_WRAP = CommonCodeStyleSettings.WRAP_ON_EVERY_ITEM
    javaSettings.WRAP_FIRST_METHOD_IN_CALL_CHAIN = true

    javaSettings.ALIGN_MULTILINE_PARAMETERS = true
    javaSettings.BLANK_LINES_AROUND_METHOD = 1

    javaSettings.BLANK_LINES_BEFORE_CLASS_END = 1
    javaSettings.BLANK_LINES_AFTER_CLASS_HEADER = 1

    val javaCustomSettings = settings.getCustomSettings(JavaCodeStyleSettings::class.java)
    javaCustomSettings.RECORD_COMPONENTS_WRAP = CommonCodeStyleSettings.WRAP_ON_EVERY_ITEM


    return settings
  }
}
