package com.javalint.formatter.lang

import com.intellij.psi.codeStyle.ProjectCodeStyleSettingsManager
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl

data class FormatterComponents(
  val projectCodeStyleSettingsManager: ProjectCodeStyleSettingsManager,
  val codeStyleManagerImpl: CodeStyleManagerImpl
)
