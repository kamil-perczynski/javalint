package io.github.kamilperczynski.javalint.ec

import com.intellij.json.JsonLanguage
import com.intellij.json.formatter.JsonCodeStyleSettings
import com.intellij.lang.Language
import com.intellij.lang.java.JavaLanguage
import com.intellij.lang.xml.XMLLanguage
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.JavaCodeStyleSettings
import com.intellij.psi.formatter.xml.XmlCodeStyleSettings
import io.github.kamilperczynski.javalint.ec.lang.*
import io.github.kamilperczynski.javalint.formatter.logging.Slf4j
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.formatter.YAMLCodeStyleSettings
import java.util.*
import java.util.stream.Collectors

private val javaECCodeStyleAdapter = JavaECCodeStyleAdapter()
private val xmlECCodeStyleAdapter = XmlECCodeStyleAdapter()
private val jsonECCodeStyleAdapter = JsonECCodeStyleAdapter()
private val yamlECCodeStyleAdapter = YAMLECCodeStyleAdapter()
private val commonECCodeStyleAdapter = CommonECCodeStyleAdapter()
private val indentOptionsECCodeStyleAdapter = IndentOptionsECCodeStyleAdapter()

class ECCodeStyleSettingsAdapter(private val codeStyleSettings: CodeStyleSettings) {

  companion object : Slf4j()

  fun setIjProperty(ecProperty: ECProperty) {
    if (!isIjProperty(ecProperty)) {
      log.debug("Unknown property: {}", ecProperty.name)
      return
    }

    val parts = ecProperty.name.split("_").toTypedArray()

    val ijProperty = Arrays.stream(parts)
      .skip(2)
      .collect(Collectors.joining("_"))

    val commonSettings = toCommonCodeStyleSettings(parts[1], codeStyleSettings)

    val parsedProperty = ECProperty(ijProperty, ecProperty.rawValue)

    commonECCodeStyleAdapter.setProperty(commonSettings, parsedProperty)
      ?: indentOptionsECCodeStyleAdapter.setProperty(commonSettings.indentOptions!!, parsedProperty)
      ?: javaECCodeStyleAdapter.setProperty(
        codeStyleSettings.getCustomSettings(JavaCodeStyleSettings::class.java),
        parsedProperty
      )
      ?: xmlECCodeStyleAdapter.setProperty(
        codeStyleSettings.getCustomSettings(XmlCodeStyleSettings::class.java),
        parsedProperty
      )
      ?: jsonECCodeStyleAdapter.setProperty(
        codeStyleSettings.getCustomSettings(JsonCodeStyleSettings::class.java),
        parsedProperty
      )
      ?: yamlECCodeStyleAdapter.setProperty(
        codeStyleSettings.getCustomSettings(YAMLCodeStyleSettings::class.java),
        parsedProperty
      )
      ?: log.warn("Unsupported property: {}", ecProperty.name)
  }

}

private fun toCommonCodeStyleSettings(
  ijPropertyLanguage: String,
  codeStyleSettings: CodeStyleSettings
): CommonCodeStyleSettings {
  return when (ijPropertyLanguage) {
    "any" -> codeStyleSettings.getCommonSettings(null as Language?)
    "java" -> codeStyleSettings.getCommonSettings(JavaLanguage.INSTANCE)
    "json" -> codeStyleSettings.getCommonSettings(JsonLanguage.INSTANCE)
    "yaml" -> codeStyleSettings.getCommonSettings(YAMLLanguage.INSTANCE)
    "xml" -> codeStyleSettings.getCommonSettings(XMLLanguage.INSTANCE)
    else -> throw IllegalArgumentException("Should not ever happen!")
  }
}

private fun isIjProperty(property: ECProperty): Boolean {
  return (property.name.startsWith("ij_any")
    || property.name.startsWith("ij_java")
    || property.name.startsWith("ij_xml")
    || property.name.startsWith("ij_json")
    || property.name.startsWith("ij_yaml"))
}
