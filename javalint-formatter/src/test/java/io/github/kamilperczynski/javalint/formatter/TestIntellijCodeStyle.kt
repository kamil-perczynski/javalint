package io.github.kamilperczynski.javalint.formatter

import com.intellij.json.JsonLanguage
import com.intellij.lang.java.JavaLanguage
import com.intellij.lang.xml.XMLLanguage
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.IndentOptions
import io.github.kamilperczynski.javalint.formatter.codestyle.JavaLintCodeStyle
import org.jetbrains.yaml.YAMLLanguage
import java.nio.file.Path
import java.util.function.Supplier

val supportedLanguages = arrayOf(
  JavaLanguage.INSTANCE,
  JsonLanguage.INSTANCE,
  XMLLanguage.INSTANCE,
  YAMLLanguage.INSTANCE
)

class TestIntellijCodeStyle(private val indentOptions: IndentOptions) : JavaLintCodeStyle {

  override fun configure(
    file: Path,
    settingsSupplier: Supplier<CodeStyleSettings>
  ): CodeStyleSettings {
    val settings = settingsSupplier.get()

    for (language in supportedLanguages) {
      settings.getCommonSettings(language).also {
        it.indentOptions!!.INDENT_SIZE = indentOptions.INDENT_SIZE
        it.indentOptions!!.CONTINUATION_INDENT_SIZE = indentOptions.CONTINUATION_INDENT_SIZE
      }
    }
    return settings
  }

}
