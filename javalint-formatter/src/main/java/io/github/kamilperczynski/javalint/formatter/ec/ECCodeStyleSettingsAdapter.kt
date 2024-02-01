package io.github.kamilperczynski.javalint.formatter.ec

import com.intellij.application.options.codeStyle.properties.*
import com.intellij.psi.codeStyle.CodeStyleSettings
import io.github.kamilperczynski.javalint.formatter.ec.PropertyAssignmentResult.*
import io.github.kamilperczynski.javalint.formatter.logging.Slf4j
import java.util.*
import java.util.stream.Collectors

class ECCodeStyleSettingsAdapter(codeStyleSettings: CodeStyleSettings) {

  companion object : Slf4j()

  private val settingsAccessors = ECCodeStyleSettingsAccessors(codeStyleSettings)

  fun setIjProperty(ecProperty: ECProperty) {
    if (!isIjProperty(ecProperty)) {
      log.debug("Unknown property: {}", ecProperty.name)
      return
    }

    val parts = ecProperty.name
      .split("_")
      .toTypedArray()

    val ijProperty = Arrays
      .stream(parts)
      .skip(2)
      .collect(Collectors.joining("_"))

    val ijLang = parts[1]
    val parsedProperty = ECProperty(ijProperty, ecProperty.value)

    if (ijLang == "any") {
      val assignment = settingsAccessors.setCommonProperty(parsedProperty)
      if (assignment != ASSIGNED) {
        log.warn("Unsupported property: {}", ecProperty.name)
      }
      return
    }

    when (settingsAccessors.setLanguageProperty(ijLang, parsedProperty)) {
      ASSIGNED -> return
      ACCESSOR_MISSING -> {}
      INVALID_VALUE -> logInvalidValue(
        ecProperty,
        settingsAccessors.languagePropertyAccessor(ijLang, parsedProperty.name)
      )
    }

    when (settingsAccessors.setCommonProperty(ijLang, parsedProperty)) {
      ASSIGNED -> return
      ACCESSOR_MISSING -> log.warn("Unsupported property: {}", ecProperty.name)
      INVALID_VALUE -> logInvalidValue(
        ecProperty,
        settingsAccessors.commonPropertyAccessor(parsedProperty.name)
      )
    }
  }

  private fun logInvalidValue(ecProperty: ECProperty, accessor: CodeStylePropertyAccessor<*>?) {
    if (accessor == null) {
      return
    }

    when (accessor) {
      is EnumPropertyAccessor ->
        log.warn(
          "Invalid value for property: {}, expected one of {} but was <{}>",
          ecProperty.name,
          accessor.choices,
          ecProperty.value
        )

      is CodeStyleChoiceList ->
        log.warn(
          "Invalid value for property: {}, expected one of {} but was <{}>",
          ecProperty.name,
          accessor.choices,
          ecProperty.value
        )

      is IntegerAccessor ->
        log.warn(
          "Invalid value for property: {}, expected integer but was <{}>",
          ecProperty.name,
          ecProperty.value
        )

      is StringAccessor ->
        log.warn(
          "Invalid value for property: {}, expected string but was <{}>",
          ecProperty.name,
          ecProperty.value
        )

      else ->
        log.warn(
          "Invalid value for property: {}, expected {} but was <{}>",
          ecProperty.name,
          accessor.javaClass,
          ecProperty.value
        )
    }
  }

  fun setCommonProperty(property: ECProperty) {
    val result = settingsAccessors.setCommonProperty(property)

    when (result) {
      ASSIGNED -> return
      ACCESSOR_MISSING -> log.warn("Unsupported property: {}", property.name)
      INVALID_VALUE -> logInvalidValue(
        property,
        settingsAccessors.commonPropertyAccessor(property.name)
      )
    }
  }

}

private fun isIjProperty(property: ECProperty): Boolean {
  return (property.name.startsWith("ij_any")
    || property.name.startsWith("ij_java")
    || property.name.startsWith("ij_xml")
    || property.name.startsWith("ij_json")
    || property.name.startsWith("ij_yaml"))
}
