package com.javalint.ec.settings

import com.intellij.lang.Language
import com.intellij.lang.xml.XMLLanguage
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsManager
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.*
import com.intellij.psi.codeStyle.JavaCodeStyleSettings
import com.intellij.psi.formatter.xml.XmlCodeStyleSettings
import com.javalint.formatter.IntellijFormatter
import com.javalint.formatter.IntellijFormatterOptions
import com.javalint.formatter.lang.JavaFormatterLanguage
import com.javalint.formatter.lang.JsonFormatterLanguage
import com.javalint.formatter.lang.XmlFormatterLanguage
import com.javalint.formatter.lang.YamlFormatterLanguage
import com.javalint.formatter.output.FixFormattingCommandEvents
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.formatter.YAMLCodeStyleSettings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

class ECCodeStyleTest {

  companion object {

    private lateinit var intellijFormatter: IntellijFormatter
    private val baseDir = Paths.get(".").toRealPath()

    @JvmStatic
    @BeforeAll
    fun beforeAll() {
      intellijFormatter = IntellijFormatter(
        IntellijFormatterOptions(
          baseDir,
          FixFormattingCommandEvents(baseDir)
        )
      )
    }

  }

  @Test
  fun testParseECFileSettings() {
    // given:
    val ecProperties = ECFile(baseDir.resolve("src/test/resources"))
    val ecCodeStyle = ECCodeStyle(ecProperties)

    val file = Paths.get("ECCodeStyle.java")

    val codeStyleSettings = createCodeSettings()

    // when:
    val charset = ecCodeStyle.charset(file)
    val configuredSettings = ecCodeStyle.configure(file, codeStyleSettings)

    // then:
    assertEquals(StandardCharsets.ISO_8859_1, charset)

    assertEquals(4, configuredSettings.indentOptions.INDENT_SIZE)
    assertEquals(4, configuredSettings.indentOptions.CONTINUATION_INDENT_SIZE)

    val commonSettings = configuredSettings.getCommonSettings(null as Language?)

    assertEquals(WRAP_ON_EVERY_ITEM, commonSettings.METHOD_CALL_CHAIN_WRAP)
    assertEquals(true, commonSettings.WRAP_FIRST_METHOD_IN_CALL_CHAIN)
    assertEquals(WRAP_ALWAYS, commonSettings.CALL_PARAMETERS_WRAP)

    assertEquals(WRAP_ON_EVERY_ITEM, commonSettings.METHOD_PARAMETERS_WRAP)
    assertEquals(true, commonSettings.PREFER_PARAMETERS_WRAP)

    assertEquals(WRAP_ON_EVERY_ITEM, commonSettings.TERNARY_OPERATION_WRAP)
    assertEquals(true, commonSettings.TERNARY_OPERATION_SIGNS_ON_NEXT_LINE)
  }

  @Test
  fun testReadCustomJavaSettings() {
    // given:
    val ecProperties = ECFile(baseDir.resolve("src/test/resources"))
    val ecCodeStyle = ECCodeStyle(ecProperties)

    val file = Paths.get("ECCodeStyle.java")
    val codeStyleSettings = createCodeSettings()

    // when:
    val configuredSettings = ecCodeStyle.configure(file, codeStyleSettings)

    // then:
    val javaSettings = configuredSettings.getCustomSettings(JavaCodeStyleSettings::class.java)

    assertEquals(true, javaSettings.ALIGN_MULTILINE_RECORDS)
    assertEquals(WRAP_ALWAYS, javaSettings.RECORD_COMPONENTS_WRAP)
  }

  @Test
  fun testReadYamlSettings() {
    // given:
    val ecProperties = ECFile(baseDir.resolve("src/test/resources"))
    val ecCodeStyle = ECCodeStyle(ecProperties)

    val file = Paths.get("application.yaml")
    val codeStyleSettings = createCodeSettings()

    // when:
    val configuredSettings = ecCodeStyle.configure(file, codeStyleSettings)

    // then:
    val yamlCommonSettings = configuredSettings.getCommonSettings(YAMLLanguage.INSTANCE)
    val yamlCustomSettings = configuredSettings.getCustomSettings(YAMLCodeStyleSettings::class.java)

    assertEquals(2, yamlCommonSettings.indentOptions!!.INDENT_SIZE)
    assertEquals(false, yamlCustomSettings.INDENT_SEQUENCE_VALUE)
  }

  @Test
  fun testReadXmlSettings() {
    // given:
    val ecProperties = ECFile(baseDir.resolve("src/test/resources"))
    val ecCodeStyle = ECCodeStyle(ecProperties)

    val file = Paths.get("pom.xml")
    val codeStyleSettings = createCodeSettings()

    // when:
    val configuredSettings = ecCodeStyle.configure(file, codeStyleSettings)

    // then:
    val xmlCommonSettings = configuredSettings.getCommonSettings(XMLLanguage.INSTANCE)
    val xmlCustomSettings = configuredSettings.getCustomSettings(XmlCodeStyleSettings::class.java)

    assertEquals(3, xmlCommonSettings.indentOptions!!.INDENT_SIZE)
    assertEquals(WRAP_ALWAYS, xmlCustomSettings.XML_ATTRIBUTE_WRAP)
    assertEquals(true, xmlCustomSettings.XML_KEEP_WHITESPACES)
  }

}

private fun createCodeSettings(): CodeStyleSettings {
  val codeStyleSettings = CodeStyleSettingsManager.createTestSettings(null)
  JavaFormatterLanguage().configureCodeStyleSettings(codeStyleSettings)
  XmlFormatterLanguage().configureCodeStyleSettings(codeStyleSettings)
  JsonFormatterLanguage().configureCodeStyleSettings(codeStyleSettings)
  YamlFormatterLanguage().configureCodeStyleSettings(codeStyleSettings)
  return codeStyleSettings
}
