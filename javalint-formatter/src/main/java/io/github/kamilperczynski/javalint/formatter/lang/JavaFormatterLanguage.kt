package io.github.kamilperczynski.javalint.formatter.lang

import com.intellij.core.CoreFileTypeRegistry
import com.intellij.ide.JavaLanguageCodeStyleSettingsProvider
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.LanguageFormatting
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.lang.java.JavaFormattingModelBuilder
import com.intellij.lang.java.JavaLanguage
import com.intellij.lang.java.JavaParserDefinition
import com.intellij.mock.MockProject
import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.extensions.ExtensionsArea
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.impl.PsiJavaModuleTreeChangePreprocessor
import com.intellij.psi.impl.PsiTreeChangePreprocessor
import com.intellij.psi.impl.source.tree.JavaASTFactory

class JavaFormatterLanguage : FormatterLanguage {

  override fun registerLanguageComponents() {
    LanguageParserDefinitions.INSTANCE.addExplicitExtension(
      JavaLanguage.INSTANCE,
      JavaParserDefinition()
    )
    LanguageFormatting.INSTANCE.addExplicitExtension(
      JavaLanguage.INSTANCE,
      JavaFormattingModelBuilder()
    )

    LanguageASTFactory.INSTANCE.addExplicitExtension(JavaLanguage.INSTANCE, JavaASTFactory())
    LanguageASTFactory.INSTANCE.addExplicitExtension(JavaLanguage.INSTANCE, JavaASTFactory())
  }

  override fun configureCodeStyleSettings(codeStyleSettings: CodeStyleSettings) {
    codeStyleSettings.registerCustomSettings(JavaLanguageCodeStyleSettingsProvider())
  }

  override fun registerProjectExtensions(extensionsArea: ExtensionsArea) {
    extensionsArea.registerExtensionPoint(
      PsiTreeChangePreprocessor.EP.name,
      PsiJavaModuleTreeChangePreprocessor::class.java.name,
      ExtensionPoint.Kind.BEAN_CLASS,
      false
    )
  }

  override fun registerProjectComponents(project: MockProject) {
  }

  override fun registerFileType(fileTypeRegistry: CoreFileTypeRegistry) {
    fileTypeRegistry.registerFileType(JavaFileType.INSTANCE, JavaFileType.DEFAULT_EXTENSION)
  }
}
