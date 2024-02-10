package io.github.kamilperczynski.javalint.formatter.ec

import com.intellij.application.options.codeStyle.properties.CodeStyleChoiceList
import com.intellij.application.options.codeStyle.properties.CodeStylePropertyAccessor
import com.intellij.application.options.codeStyle.properties.CodeStyleValueList
import com.intellij.application.options.codeStyle.properties.GeneralCodeStylePropertyMapper
import com.intellij.application.options.codeStyle.properties.IntegerAccessor
import com.intellij.application.options.codeStyle.properties.StringAccessor
import com.intellij.json.JsonLanguage
import com.intellij.json.formatter.JsonCodeStyleSettings
import com.intellij.lang.java.JavaLanguage
import com.intellij.lang.xml.XMLLanguage
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.JavaCodeStyleSettings
import com.intellij.psi.formatter.xml.XmlCodeStyleSettings
import io.github.kamilperczynski.javalint.formatter.ec.ECPropertyAssignment.ACCESSOR_MISSING
import io.github.kamilperczynski.javalint.formatter.ec.ECPropertyAssignment.ASSIGNED
import io.github.kamilperczynski.javalint.formatter.ec.ECPropertyAssignment.INVALID_VALUE
import io.github.kamilperczynski.javalint.formatter.lang.javaLanguageCodeStyleSettingsProvider
import io.github.kamilperczynski.javalint.formatter.lang.jsonLanguageCodeStyleSettingsProvider
import io.github.kamilperczynski.javalint.formatter.lang.xmlLanguageCodeStyleSettingsProvider
import io.github.kamilperczynski.javalint.formatter.lang.yamlLanguageCodeStyleSettingsProvider
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.formatter.YAMLCodeStyleSettings
import kotlin.math.abs

class ECCodeStyleSettingsAccessors(codeStyleSettings: CodeStyleSettings) {

  private val rootSettings = GeneralCodeStylePropertyMapper(codeStyleSettings)

  private val commonSettings = mapOf(
    "java" to CommonCodeStylePropertyMapper(codeStyleSettings.getCommonSettings(JavaLanguage.INSTANCE)),
    "xml" to CommonCodeStylePropertyMapper(codeStyleSettings.getCommonSettings(XMLLanguage.INSTANCE)),
    "json" to CommonCodeStylePropertyMapper(codeStyleSettings.getCommonSettings(JsonLanguage.INSTANCE)),
    "yaml" to CommonCodeStylePropertyMapper(codeStyleSettings.getCommonSettings(YAMLLanguage.INSTANCE)),
  )

  private val customSettings = mapOf(
    "java" to CustomCodeStylePropertyMapper(
      codeStyleSettings.getCustomSettings(JavaCodeStyleSettings::class.java),
      javaLanguageCodeStyleSettingsProvider
    ),
    "xml" to CustomCodeStylePropertyMapper(
      codeStyleSettings.getCustomSettings(XmlCodeStyleSettings::class.java),
      xmlLanguageCodeStyleSettingsProvider
    ),
    "json" to CustomCodeStylePropertyMapper(
      codeStyleSettings.getCustomSettings(JsonCodeStyleSettings::class.java),
      jsonLanguageCodeStyleSettingsProvider
    ),
    "yaml" to CustomCodeStylePropertyMapper(
      codeStyleSettings.getCustomSettings(YAMLCodeStyleSettings::class.java),
      yamlLanguageCodeStyleSettingsProvider
    ),
  )

  fun setLanguageProperty(lang: String, ecProperty: ECProperty): ECPropertyAssignment {
    val accessor = customSettings[lang]!!.getAccessor(ecProperty.name)

    return assignCodeStyleProperty(accessor, ecProperty)
  }

  fun setCommonProperty(lang: String, ecProperty: ECProperty): ECPropertyAssignment {
    val accessor = commonSettings[lang]!!.getAccessor(ecProperty.name)

    return assignCodeStyleProperty(accessor, ecProperty)
  }

  fun setCommonProperty(ecProperty: ECProperty): ECPropertyAssignment {
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

  fun commonSettingsDescriptors(): List<CodeStyleSettingDescriptor> {
    val commonCodeStylePropertyMapper = commonSettings.values.first()

    return commonCodeStylePropertyMapper.enumProperties().map {
      toCodeStyleSettingDescriptor(it, commonCodeStylePropertyMapper.getAccessor(it))
    }
  }

  fun languageSettingsDescriptor(lang: String): List<CodeStyleSettingDescriptor> {
    val commonCodeStylePropertyMapper = customSettings[lang]
      ?: throw IllegalArgumentException("Unknown language $lang")

    return commonCodeStylePropertyMapper.enumProperties().map {
      toCodeStyleSettingDescriptor(it, commonCodeStylePropertyMapper.getAccessor(it))
    }
  }

  fun rootSettingsDescriptor(): List<CodeStyleSettingDescriptor> {
    return rootSettings.enumProperties().map {
      toCodeStyleSettingDescriptor(it, rootSettings.getAccessor(it))
    }
  }

}

private fun toCodeStyleSettingDescriptor(
  property: String,
  accessor: CodeStylePropertyAccessor<*>?
): CodeStyleSettingDescriptor {
  if (property == "indent_style") {
    return EnumCodeStyleSettingDescriptor(property, listOf("space", "tab"))
  }

  return when (accessor) {
    is CodeStyleChoiceList -> EnumCodeStyleSettingDescriptor(property, accessor.choices)
    is StringAccessor -> PrimitiveCodeStyleSettingDescriptor(property, "string", "string")
    is IntegerAccessor -> PrimitiveCodeStyleSettingDescriptor(
      property,
      "integer",
      (abs(property.hashCode()) % 5).toString()
    )

    is CodeStyleValueList -> PrimitiveCodeStyleSettingDescriptor(property, "values list", "none")
    else -> PrimitiveCodeStyleSettingDescriptor(property, "unknown", "")
  }
}

interface CodeStyleSettingDescriptor {
  val name: String
  val options: List<String>
  val exampleValue: String
}

data class EnumCodeStyleSettingDescriptor(
  override val name: String,
  override val options: List<String>,
) : CodeStyleSettingDescriptor {

  override val exampleValue: String
    get() = options[0]

}

data class PrimitiveCodeStyleSettingDescriptor(
  override val name: String,
  val option: String,
  override val exampleValue: String
) : CodeStyleSettingDescriptor {

  override val options: List<String>
    get() = listOf(option)

}


private fun assignCodeStyleProperty(
  accessor: CodeStylePropertyAccessor<*>?,
  property: ECProperty
): ECPropertyAssignment {
  return accessor
    ?.let {
      if (accessor.setFromString(property.value)) ASSIGNED
      else INVALID_VALUE
    }
    ?: ACCESSOR_MISSING
}
