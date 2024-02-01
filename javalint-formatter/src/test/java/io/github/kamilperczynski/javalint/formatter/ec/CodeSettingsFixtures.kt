package io.github.kamilperczynski.javalint.formatter.ec

import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsManager
import io.github.kamilperczynski.javalint.formatter.lang.JavaFormatterLanguage
import io.github.kamilperczynski.javalint.formatter.lang.JsonFormatterLanguage
import io.github.kamilperczynski.javalint.formatter.lang.XmlFormatterLanguage
import io.github.kamilperczynski.javalint.formatter.lang.YamlFormatterLanguage


fun someCodeSettings(): CodeStyleSettings {
  val codeStyleSettings = CodeStyleSettingsManager.createTestSettings(null)
  JavaFormatterLanguage().configureCodeStyleSettings(codeStyleSettings)
  XmlFormatterLanguage().configureCodeStyleSettings(codeStyleSettings)
  JsonFormatterLanguage().configureCodeStyleSettings(codeStyleSettings)
  YamlFormatterLanguage().configureCodeStyleSettings(codeStyleSettings)
  return codeStyleSettings
}
