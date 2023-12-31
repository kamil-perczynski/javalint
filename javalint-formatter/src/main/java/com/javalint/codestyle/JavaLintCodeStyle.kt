package com.javalint.codestyle

import com.intellij.psi.codeStyle.CodeStyleSettings

interface JavaLintCodeStyle {
  fun configure(settings: CodeStyleSettings): CodeStyleSettings;
}
