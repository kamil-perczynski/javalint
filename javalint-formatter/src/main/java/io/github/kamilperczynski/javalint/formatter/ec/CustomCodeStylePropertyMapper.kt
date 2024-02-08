@file:Suppress("UnstableApiUsage")

package io.github.kamilperczynski.javalint.formatter.ec

import com.intellij.application.options.codeStyle.properties.AbstractCodeStylePropertyMapper
import com.intellij.application.options.codeStyle.properties.CodeStylePropertyAccessor
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import java.lang.reflect.Field

class CustomCodeStylePropertyMapper(
  private val customSettings: CustomCodeStyleSettings,
  private val settingsProvider: LanguageCodeStyleSettingsProvider
) : AbstractCodeStylePropertyMapper(customSettings.container) {

  override fun getSupportedFields(): MutableList<CodeStyleObjectDescriptor> {
    return mutableListOf(CodeStyleObjectDescriptor(customSettings, null))
  }

  override fun getAccessor(codeStyleObject: Any, field: Field): CodeStylePropertyAccessor<*>? {
    return settingsProvider.getAccessor(codeStyleObject, field)
      ?: super.getAccessor(codeStyleObject, field)
  }

  override fun getLanguageDomainId(): String = settingsProvider.language.id.lowercase()
  override fun getPropertyDescription(externalName: String): String? = null

}
