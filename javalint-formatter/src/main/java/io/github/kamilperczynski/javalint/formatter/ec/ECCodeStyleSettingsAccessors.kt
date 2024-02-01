package io.github.kamilperczynski.javalint.formatter.ec

import com.intellij.application.options.XmlLanguageCodeStyleSettingsProvider
import com.intellij.application.options.codeStyle.properties.CodeStylePropertyAccessor
import com.intellij.application.options.codeStyle.properties.GeneralCodeStylePropertyMapper
import com.intellij.ide.JavaLanguageCodeStyleSettingsProvider
import com.intellij.json.JsonLanguage
import com.intellij.json.formatter.JsonCodeStyleSettings
import com.intellij.json.formatter.JsonLanguageCodeStyleSettingsProvider
import com.intellij.lang.java.JavaLanguage
import com.intellij.lang.xml.XMLLanguage
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.JavaCodeStyleSettings
import com.intellij.psi.formatter.xml.XmlCodeStyleSettings
import io.github.kamilperczynski.javalint.formatter.ec.PropertyAssignmentResult.*
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.YAMLLanguageCodeStyleSettingsProvider
import org.jetbrains.yaml.formatter.YAMLCodeStyleSettings

class ECCodeStyleSettingsAccessors(codeStyleSettings: CodeStyleSettings) {

  internal val rootSettings = GeneralCodeStylePropertyMapper(codeStyleSettings)

  internal val commonSettings = mapOf(
    "java" to CommonCodeStylePropertyMapper(codeStyleSettings.getCommonSettings(JavaLanguage.INSTANCE)),
    "xml" to CommonCodeStylePropertyMapper(codeStyleSettings.getCommonSettings(XMLLanguage.INSTANCE)),
    "json" to CommonCodeStylePropertyMapper(codeStyleSettings.getCommonSettings(JsonLanguage.INSTANCE)),
    "yaml" to CommonCodeStylePropertyMapper(codeStyleSettings.getCommonSettings(YAMLLanguage.INSTANCE)),
  )

  internal val customSettings = mapOf(
    "java" to CustomCodeStylePropertyMapper(
      codeStyleSettings.getCustomSettings(JavaCodeStyleSettings::class.java),
      JavaLanguageCodeStyleSettingsProvider()
    ),
    "xml" to CustomCodeStylePropertyMapper(
      codeStyleSettings.getCustomSettings(XmlCodeStyleSettings::class.java),
      XmlLanguageCodeStyleSettingsProvider()
    ),
    "json" to CustomCodeStylePropertyMapper(
      codeStyleSettings.getCustomSettings(JsonCodeStyleSettings::class.java),
      JsonLanguageCodeStyleSettingsProvider()
    ),
    "yaml" to CustomCodeStylePropertyMapper(
      codeStyleSettings.getCustomSettings(YAMLCodeStyleSettings::class.java),
      YAMLLanguageCodeStyleSettingsProvider()
    ),
  )

  fun setLanguageProperty(lang: String, ecProperty: ECProperty): PropertyAssignmentResult {
    val accessor = customSettings[lang]!!.getAccessor(ecProperty.name)

    return assignCodeStyleProperty(accessor, ecProperty)
  }

  fun setCommonProperty(lang: String, ecProperty: ECProperty): PropertyAssignmentResult {
    val accessor = commonSettings[lang]!!.getAccessor(ecProperty.name)

    return assignCodeStyleProperty(accessor, ecProperty)
  }

  fun setCommonProperty(ecProperty: ECProperty): PropertyAssignmentResult {
    val rootAccessor = rootSettings.getAccessor(ecProperty.name)
    val rootSettingsAssignment = assignCodeStyleProperty(rootAccessor, ecProperty)

    val commonSettingsAssignment = commonSettings.values
      .map { assignCodeStyleProperty(it.getAccessor(ecProperty.name), ecProperty) }
      .minByOrNull { it.ordinal }
      ?: ACCESSOR_MISSING

    return if (rootSettingsAssignment.ordinal < commonSettingsAssignment.ordinal)
      rootSettingsAssignment
    else
      commonSettingsAssignment
  }

  fun commonPropertyAccessor(property: String): CodeStylePropertyAccessor<*>? {
    return commonSettings.values.first().getAccessor(property)
      ?: rootSettings.getAccessor(property)
  }

  fun languagePropertyAccessor(lang: String, property: String): CodeStylePropertyAccessor<*>? {
    return customSettings[lang]?.getAccessor(property)
  }

}

enum class PropertyAssignmentResult {
  ASSIGNED,
  INVALID_VALUE,
  ACCESSOR_MISSING
}

private fun assignCodeStyleProperty(
  accessor: CodeStylePropertyAccessor<*>?,
  property: ECProperty
): PropertyAssignmentResult {
  return accessor
    ?.let {
      if (accessor.setFromString(property.value)) ASSIGNED
      else INVALID_VALUE
    }
    ?: ACCESSOR_MISSING
}
