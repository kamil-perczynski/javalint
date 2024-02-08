package io.github.kamilperczynski.javalint.formatter.ec

import com.intellij.application.options.codeStyle.properties.CodeStyleChoiceList
import com.intellij.application.options.codeStyle.properties.CodeStylePropertyAccessor
import com.intellij.application.options.codeStyle.properties.IntegerAccessor
import com.intellij.application.options.codeStyle.properties.StringAccessor
import com.intellij.psi.codeStyle.CodeStyleSettings
import io.github.kamilperczynski.javalint.formatter.ec.ECPropertyAssignment.*
import io.github.kamilperczynski.javalint.formatter.logging.Slf4j
import java.util.*
import java.util.stream.Collectors

class ECCodeStyleSettingsAdapter(codeStyleSettings: CodeStyleSettings) {

  companion object : Slf4j()

  private val settingsAccessors = ECCodeStyleSettingsAccessors(codeStyleSettings)

  fun setIjProperty(ecProperty: ECProperty): ECPropertyAssignment {
    if (!isIjProperty(ecProperty)) {
      log.debug("Unknown property: {}", ecProperty.name)
      return ACCESSOR_MISSING
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

      return when (assignment) {
        ASSIGNED -> ASSIGNED
        ACCESSOR_MISSING -> {
          log.warn("Unsupported property: {}", ecProperty.name)
          ACCESSOR_MISSING
        }

        INVALID_VALUE -> {
          val accessor = settingsAccessors.commonPropertyAccessor(parsedProperty.name)
          logInvalidValue(ecProperty, accessor)

          INVALID_VALUE
        }
      }
    }

    when (settingsAccessors.setLanguageProperty(ijLang, parsedProperty)) {
      ASSIGNED -> return ASSIGNED
      ACCESSOR_MISSING -> {}
      INVALID_VALUE -> {
        val accessor = settingsAccessors.languagePropertyAccessor(ijLang, parsedProperty.name)
        logInvalidValue(ecProperty, accessor)

        return INVALID_VALUE
      }
    }

    return when (settingsAccessors.setCommonProperty(ijLang, parsedProperty)) {
      ASSIGNED -> ASSIGNED
      ACCESSOR_MISSING -> {
        log.warn("Unsupported property: {}", ecProperty.name)
        ACCESSOR_MISSING
      }

      INVALID_VALUE -> {
        val accessor = settingsAccessors.commonPropertyAccessor(parsedProperty.name)
        logInvalidValue(ecProperty, accessor)

        INVALID_VALUE
      }
    }
  }

  private fun logInvalidValue(ecProperty: ECProperty, accessor: CodeStylePropertyAccessor<*>?) {
    if (accessor == null) {
      return
    }

    when (accessor) {
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

  fun setCommonProperty(property: ECProperty): ECPropertyAssignment {
    val commonProperty = if (property.name.startsWith("ij_"))
      property.copy(name = property.name.substring(3))
    else
      property

    val result = settingsAccessors.setCommonProperty(commonProperty)

    return when (result) {
      ASSIGNED -> ASSIGNED
      ACCESSOR_MISSING -> {
        log.warn("Unsupported property: {}", property.name)
        ACCESSOR_MISSING
      }

      INVALID_VALUE -> {
        val accessor = settingsAccessors.commonPropertyAccessor(commonProperty.name)
        logInvalidValue(property, accessor)

        INVALID_VALUE
      }
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
