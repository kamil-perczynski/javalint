package io.github.kamilperczynski.javalint.formatter.ec

import com.intellij.psi.codeStyle.CodeStyleSettings
import io.github.kamilperczynski.javalint.formatter.codestyle.JavaLintCodeStyle
import io.github.kamilperczynski.javalint.formatter.logging.Slf4j
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.function.Supplier

class ECCodeStyle(private val ecSource: ECSource) : JavaLintCodeStyle {

  private var lastEcProperties: Set<ECProperty>? = null
  private var lastCodeStyleSettings: CodeStyleSettings? = null

  companion object : Slf4j()

  override fun charset(file: Path): Charset {
    val ecCharsetValue = ecSource.charset(file)

    return toCharset(ecCharsetValue)
  }

  override fun configure(
    file: Path,
    settingsSupplier: Supplier<CodeStyleSettings>
  ): CodeStyleSettings {
    val ecProperties = ecSource.findECProps(file)

    if (ecProperties == lastEcProperties) {
      log.debug("Reusing cached code style settings")
      return lastCodeStyleSettings!!
    }
    
    val settings = settingsSupplier.get()
    val codeStyleSettings = configureCodeStyleSettings(settings, ecProperties)

    lastCodeStyleSettings = codeStyleSettings
    lastEcProperties = ecProperties

    return codeStyleSettings
  }

  private fun configureCodeStyleSettings(
    settings: CodeStyleSettings,
    editorConfigProperties: Set<ECProperty>
  ): CodeStyleSettings {
    val codeStyleSettingsAdapter = ECCodeStyleSettingsAdapter(settings)

    for (property in editorConfigProperties) {
      when (property.name) {
        "charset" ->
          log.trace("Charset property is ignored by ECCodeStyle")

        "indent_style", "tab_width", "max_line_length" ->
          codeStyleSettingsAdapter.setCommonProperty(property)

        "trim_trailing_whitespace" ->
          log.trace("Trailing whitespace is obligatorily trimmed by IJ")

        "insert_final_newline" ->
          log.trace("insert_final_newline is not supported by IJ. https://youtrack.jetbrains.com/issue/IDEA-320289")

        "indent_size" -> {
          codeStyleSettingsAdapter.setCommonProperty(property)
          codeStyleSettingsAdapter.setCommonProperty(
            ECProperty("continuation_indent_size", property.value)
          )
        }

        "ij_continuation_indent_size",
        "ij_formatter_enabled",
        "ij_formatter_off_tag",
        "ij_formatter_tags_enabled",
        "ij_smart_tabs",
        "ij_visual_guides",
        "ij_wrap_on_typing" -> codeStyleSettingsAdapter.setCommonProperty(property)

        "end_of_line" -> {
          log.debug("EC property end_of_line is not supported in IJ 2023.3. https://youtrack.jetbrains.com/issue/IDEA-285800")
          codeStyleSettingsAdapter.setCommonProperty(property)
        }

        else -> codeStyleSettingsAdapter.setIjProperty(property)
      }
    }

    return settings
  }

}

private fun toCharset(value: String): Charset {
  return when (value) {
    "utf-8" -> StandardCharsets.UTF_8
    "utf-8-bom" -> StandardCharsets.UTF_8
    "utf-16be" -> StandardCharsets.UTF_16BE
    "utf-16le" -> StandardCharsets.UTF_16LE
    "latin1" -> StandardCharsets.ISO_8859_1
    else -> throw IllegalArgumentException("Illegal charset value: $value")
  }
}
