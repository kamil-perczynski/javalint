package io.github.kamilperczynski.javalint.formatter.lang

import com.intellij.core.CoreFileTypeRegistry
import com.intellij.lang.LanguageFormatting
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.mock.MockProject
import com.intellij.openapi.extensions.ExtensionsArea
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.jetbrains.yaml.YAMLFileType
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.YAMLLanguageCodeStyleSettingsProvider
import org.jetbrains.yaml.YAMLParserDefinition
import org.jetbrains.yaml.formatter.YAMLFormattingModelBuilder

@Suppress("UnstableApiUsage")
class YamlFormatterLanguage : FormatterLanguage {

  override fun registerLanguageComponents() {
    LanguageParserDefinitions.INSTANCE.addExplicitExtension(
      YAMLLanguage.INSTANCE,
      YAMLParserDefinition()
    )

    LanguageFormatting.INSTANCE.addExplicitExtension(
      YAMLLanguage.INSTANCE,
      YAMLFormattingModelBuilder()
    )
  }

  override fun configureCodeStyleSettings(codeStyleSettings: CodeStyleSettings) {
    codeStyleSettings.registerCustomSettings(YAMLLanguageCodeStyleSettingsProvider())
    codeStyleSettings.registerCommonSettings(YAMLLanguageCodeStyleSettingsProvider())
  }

  override fun registerProjectExtensions(extensionsArea: ExtensionsArea) {

  }

  override fun registerProjectComponents(project: MockProject) {
  }

  override fun registerFileType(fileTypeRegistry: CoreFileTypeRegistry) {
    fileTypeRegistry.registerFileType(YAMLFileType.YML, YAMLFileType.DEFAULT_EXTENSION)
    fileTypeRegistry.registerFileType(YAMLFileType.YML, "yaml")
  }
}
