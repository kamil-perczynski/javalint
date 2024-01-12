package io.github.kamilperczynski.javalint.formatter.lang

import com.intellij.core.CoreFileTypeRegistry
import com.intellij.json.JsonFileType
import com.intellij.json.JsonLanguage
import com.intellij.json.JsonParserDefinition
import com.intellij.json.formatter.JsonCodeStyleSettingsProvider
import com.intellij.json.formatter.JsonFormattingBuilderModel
import com.intellij.json.formatter.JsonLanguageCodeStyleSettingsProvider
import com.intellij.lang.LanguageFormatting
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.mock.MockProject
import com.intellij.openapi.extensions.ExtensionsArea
import com.intellij.psi.codeStyle.CodeStyleSettings

class JsonFormatterLanguage : FormatterLanguage {

  override fun registerLanguageComponents() {
    LanguageParserDefinitions.INSTANCE.addExplicitExtension(
      JsonLanguage.INSTANCE,
      JsonParserDefinition()
    )

    LanguageFormatting.INSTANCE.addExplicitExtension(
      JsonLanguage.INSTANCE,
      JsonFormattingBuilderModel()
    )
  }

  override fun configureCodeStyleSettings(codeStyleSettings: CodeStyleSettings) {
    codeStyleSettings.registerCustomSettings(JsonLanguageCodeStyleSettingsProvider())
    codeStyleSettings.registerCustomSettings(JsonCodeStyleSettingsProvider())
  }

  override fun registerProjectExtensions(extensionsArea: ExtensionsArea) {

  }

  override fun registerProjectComponents(project: MockProject) {
  }

  override fun registerFileType(fileTypeRegistry: CoreFileTypeRegistry) {
    fileTypeRegistry.registerFileType(JsonFileType.INSTANCE, JsonFileType.DEFAULT_EXTENSION)
  }
}
