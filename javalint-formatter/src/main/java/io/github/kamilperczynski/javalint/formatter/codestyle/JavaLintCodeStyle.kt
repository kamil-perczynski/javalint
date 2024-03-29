package io.github.kamilperczynski.javalint.formatter.codestyle

import com.intellij.psi.codeStyle.CodeStyleSettings
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.function.Supplier

interface JavaLintCodeStyle {

  fun configure(file: Path, settingsSupplier: Supplier<CodeStyleSettings>): CodeStyleSettings
  fun charset(file: Path): Charset = StandardCharsets.UTF_8

}
