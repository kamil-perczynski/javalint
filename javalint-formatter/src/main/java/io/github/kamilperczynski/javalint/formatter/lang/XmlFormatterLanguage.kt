package io.github.kamilperczynski.javalint.formatter.lang

import com.intellij.application.options.XmlLanguageCodeStyleSettingsProvider
import com.intellij.core.CoreFileTypeRegistry
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.LanguageFormatting
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.lang.xml.XmlFormattingModelBuilder
import com.intellij.mock.MockProject
import com.intellij.openapi.extensions.ExtensionsArea
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.jetbrains.yaml.YAMLElementGenerator

val xmlLanguageCodeStyleSettingsProvider = XmlLanguageCodeStyleSettingsProvider()

@Suppress("UnstableApiUsage")
class XmlFormatterLanguage : FormatterLanguage {

  override fun configureCodeStyleSettings(codeStyleSettings: CodeStyleSettings) {
    codeStyleSettings.registerCustomSettings(xmlLanguageCodeStyleSettingsProvider)
    codeStyleSettings.registerCommonSettings(xmlLanguageCodeStyleSettingsProvider)
  }

  override fun registerProjectComponents(project: MockProject) {
    project.registerService(YAMLElementGenerator::class.java, YAMLElementGenerator(project))
  }

  override fun registerProjectExtensions(extensionsArea: ExtensionsArea) {
  }

  override fun registerLanguageComponents() {
    LanguageParserDefinitions.INSTANCE.addExplicitExtension(
      XMLLanguage.INSTANCE,
      XMLParserDefinition()
    )

    LanguageFormatting.INSTANCE.addExplicitExtension(
      XMLLanguage.INSTANCE,
      XmlFormattingModelBuilder()
    )

    LanguageASTFactory.INSTANCE.addExplicitExtension(XMLLanguage.INSTANCE, XmlASTFactory())
  }

  override fun registerFileType(fileTypeRegistry: CoreFileTypeRegistry) {
    fileTypeRegistry.registerFileType(XmlFileType.INSTANCE, XmlFileType.DEFAULT_EXTENSION)
  }
}
