package io.github.kamilperczynski.javalint.ec

import com.intellij.psi.codeStyle.CommonCodeStyleSettings

data class ECProperty(val name: String, val rawValue: String)

fun toBraceStylePropertyValue(property: ECProperty): Int {
  return when (property.rawValue) {
    "end_of_line" -> CommonCodeStyleSettings.END_OF_LINE
    "next_line" -> CommonCodeStyleSettings.NEXT_LINE
    "gnu" -> CommonCodeStyleSettings.NEXT_LINE_SHIFTED
    "whitesmiths" -> CommonCodeStyleSettings.NEXT_LINE_SHIFTED2
    "next_line_if_wrapped" -> CommonCodeStyleSettings.NEXT_LINE_IF_WRAPPED
    else -> throw IllegalArgumentException("Invalid value for the property: ${property.name}")
  }
}

fun toForceBracePropertyValue(property: ECProperty): Int {
  return when (property.rawValue) {
    "always" -> CommonCodeStyleSettings.FORCE_BRACES_ALWAYS
    "if_multiline" -> CommonCodeStyleSettings.FORCE_BRACES_IF_MULTILINE
    "never" -> CommonCodeStyleSettings.DO_NOT_FORCE
    else -> throw IllegalArgumentException("Invalid value for the property: ${property.name}")
  }
}

fun toWrapPropertyValue(property: ECProperty): Int {
  return when (property.rawValue) {
    "off" -> CommonCodeStyleSettings.DO_NOT_WRAP
    "normal" -> CommonCodeStyleSettings.WRAP_AS_NEEDED
    "split_into_lines" -> CommonCodeStyleSettings.WRAP_ALWAYS
    "on_every_item" -> CommonCodeStyleSettings.WRAP_ON_EVERY_ITEM
    else -> throw IllegalArgumentException("Invalid value for the property: ${property.name}")
  }
}
