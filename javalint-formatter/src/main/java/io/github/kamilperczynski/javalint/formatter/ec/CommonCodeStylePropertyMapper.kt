package io.github.kamilperczynski.javalint.formatter.ec

import com.intellij.application.options.codeStyle.properties.AbstractCodeStylePropertyMapper
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import java.util.stream.Collectors
import java.util.stream.Stream

class CommonCodeStylePropertyMapper(
  private val commonSettings: CommonCodeStyleSettings
) : AbstractCodeStylePropertyMapper(commonSettings.rootSettings) {

  override fun getSupportedFields(): MutableList<CodeStyleObjectDescriptor> {
    val commonProperties = CodeStyleObjectDescriptor(
      commonSettings,
      Stream
        .of(
          CodeStyleSettingsCustomizable.BlankLinesOption.entries.stream(),
          CodeStyleSettingsCustomizable.SpacingOption.entries.stream(),
          CodeStyleSettingsCustomizable.WrappingOrBraceOption.entries.stream(),
          CodeStyleSettingsCustomizable.CommenterOption.entries.stream(),
        )
        .flatMap { it }
        .map(Enum<*>::name)
        .collect(Collectors.toSet())
    )

    val indentSettings = CodeStyleObjectDescriptor(
      commonSettings.indentOptions as Any,
      CodeStyleSettingsCustomizable.IndentOption.entries.map { it.name }.toSet()
    )
    commonSettings.rootSettings
    return mutableListOf(commonProperties, indentSettings)
  }

  override fun getLanguageDomainId(): String = commonSettings.language.id.lowercase()
  override fun getPropertyDescription(externalName: String): String? = null

}
