package com.javalint.ec.settings

import com.intellij.configurationStore.Property
import com.intellij.json.JsonLanguage
import com.intellij.json.formatter.JsonCodeStyleSettings
import com.intellij.lang.Language
import com.intellij.lang.java.JavaLanguage
import com.intellij.lang.xml.XMLLanguage
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.*
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import com.intellij.psi.codeStyle.JavaCodeStyleSettings
import com.intellij.psi.formatter.xml.XmlCodeStyleSettings
import com.javalint.logging.Slf4j
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.formatter.YAMLCodeStyleSettings
import java.lang.reflect.Field
import java.lang.reflect.Modifier.isStatic
import java.util.*
import java.util.stream.Collectors

private val COMMON_FIELDS = detectCodeStyleFields(CommonCodeStyleSettings::class.java)
private val JAVA_FIELDS = detectCodeStyleFields(JavaCodeStyleSettings::class.java)
private val YAML_FIELDS = detectCodeStyleFields(YAMLCodeStyleSettings::class.java)
private val XML_FIELDS = detectCodeStyleFields(XmlCodeStyleSettings::class.java)
private val JSON_FIELDS = detectCodeStyleFields(JsonCodeStyleSettings::class.java)

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

    if (COMMON_FIELDS.containsKey(ijProperty)) {
      val field = COMMON_FIELDS[ijProperty]!!

      val castedValue = toCastedValue(ecProperty, field)
      logPropertyValueAssigned(ecProperty, castedValue)

      field[commonSettings] = castedValue
      return
    }

    trySetProperty(ijProperty, ecProperty, JAVA_FIELDS, JavaCodeStyleSettings::class.java)
      ?: trySetProperty(ijProperty, ecProperty, XML_FIELDS, XmlCodeStyleSettings::class.java)
      ?: trySetProperty(ijProperty, ecProperty, JSON_FIELDS, JsonCodeStyleSettings::class.java)
      ?: trySetProperty(ijProperty, ecProperty, YAML_FIELDS, YAMLCodeStyleSettings::class.java)
      ?: log.warn("Unsupported property: {}", ecProperty.name)
  }

  private fun trySetProperty(
    ijProperty: String,
    ecProperty: ECProperty,
    fields: Map<String, Field>,
    clazz: Class<out CustomCodeStyleSettings>
  ): Field? {
    if (!fields.containsKey(ijProperty)) {
      return null
    }

    val settings = codeStyleSettings.getCustomSettings(clazz)

    val field = fields[ijProperty]!!
    val castedValue = toCastedValue(ecProperty, field)
    logPropertyValueAssigned(ecProperty, castedValue)

    field[settings] = castedValue

    return field
  }

  private fun logPropertyValueAssigned(ec4jProperty: ECProperty, castedValue: Any) =
    log.debug(
      "Setting {} = {} ({})",
      ec4jProperty.name,
      castedValue,
      castedValue.javaClass.getSimpleName()
    )

}

private fun isInteger(property: ECProperty): Boolean {
  return property.rawValue.chars().allMatch { codePoint: Int -> Character.isDigit(codePoint) }
}

private fun isBoolean(property: ECProperty): Boolean {
  return when (property.rawValue) {
    "true", "false" -> true
    else -> false
  }
}

private fun toBraceStylePropertyValue(property: ECProperty): Int {
  return when (property.rawValue) {
    "end_of_line" -> END_OF_LINE
    "next_line" -> NEXT_LINE
    "gnu" -> NEXT_LINE_SHIFTED
    "whitesmiths" -> NEXT_LINE_SHIFTED2
    "next_line_if_wrapped" -> NEXT_LINE_IF_WRAPPED
    else -> throw IllegalArgumentException("Invalid value for the property: " + property.name)
  }
}

private fun toForceBracePropertyValue(property: ECProperty): Int {
  return when (property.rawValue) {
    "always" -> FORCE_BRACES_ALWAYS
    "if_multiline" -> FORCE_BRACES_IF_MULTILINE
    "never" -> DO_NOT_FORCE
    else -> throw IllegalArgumentException("Invalid value for the property: " + property.name)
  }
}

private fun toWrapPropertyValue(property: ECProperty): Int {
  return when (property.rawValue) {
    "off" -> DO_NOT_WRAP
    "normal" -> WRAP_AS_NEEDED
    "split_into_lines" -> WRAP_ALWAYS
    "on_every_item" -> WRAP_ON_EVERY_ITEM
    else -> throw IllegalArgumentException("Invalid value for the property: " + property.name)
  }
}

private fun isFieldAnnotated(field: Field, clazz: Class<out Annotation?>): Boolean {
  return field.getAnnotation(clazz) != null
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

private fun detectCodeStyleFields(clazz: Class<*>): Map<String, Field> {
  val properties: MutableMap<String, Field> = HashMap()

  for (declaredField in clazz.getDeclaredFields()) {
    if (isStatic(declaredField.modifiers) || !isScreamingCase(declaredField.name)) {
      continue
    }

    val propertyAnnotation = declaredField.getAnnotation(Property::class.java)

    val propertyName = Optional.ofNullable<Property>(propertyAnnotation)
      .map(Property::externalName)
      .orElseGet { declaredField.name.lowercase() }

    properties[propertyName] = declaredField
  }

  return properties
}

private fun isScreamingCase(name: String): Boolean {
  return name.chars().allMatch { it: Int -> it == '_'.code || Character.isUpperCase(it) }
}

private fun toCastedValue(property: ECProperty, field: Field): Any {
  if (isBoolean(property)) {
    return property.rawValue.toBoolean()
  }

  if (isInteger(property)) {
    return property.rawValue.toInt()
  }

  if (isFieldAnnotated(field, WrapConstant::class.java)) {
    return toWrapPropertyValue(property)
  }

  if (isFieldAnnotated(field, ForceBraceConstant::class.java)) {
    return toForceBracePropertyValue(property)
  }

  if (isFieldAnnotated(field, BraceStyleConstant::class.java)) {
    return toBraceStylePropertyValue(property)
  }

  return property.rawValue
}
