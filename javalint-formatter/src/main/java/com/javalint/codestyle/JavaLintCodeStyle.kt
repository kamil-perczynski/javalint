package com.javalint.codestyle

import com.intellij.psi.codeStyle.CodeStyleSettings
import java.nio.file.Path

interface JavaLintCodeStyle {

  fun configure(file: Path, settings: CodeStyleSettings): CodeStyleSettings

}
