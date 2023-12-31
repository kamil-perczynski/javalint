package com.javalint.formatter.lang

import com.intellij.core.CoreFileTypeRegistry
import com.intellij.mock.MockProject
import com.intellij.openapi.extensions.ExtensionsArea
import com.intellij.psi.codeStyle.CodeStyleSettings

interface FormatterLanguage {

  fun registerLanguageComponents() {}
  fun registerFileType(fileTypeRegistry: CoreFileTypeRegistry)
  fun configureCodeStyleSettings(codeStyleSettings: CodeStyleSettings)
  fun registerProjectComponents(project: MockProject)

  fun registerProjectExtensions(extensionsArea: ExtensionsArea)

}
