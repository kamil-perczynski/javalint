package io.github.kamilperczynski.javalint.formatter.ec

import io.github.kamilperczynski.javalint.formatter.IntellijFormatter
import io.github.kamilperczynski.javalint.formatter.NoopFormattingEvents
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.file.Paths

val cols = listOf("Property", "Possible values", "Example")
val widths = listOf(55, 65, 10)

class ECCodeStyleSettingsAccessorsTest {

  private var mdTable: MdTable? = null

  companion object {
    private lateinit var formatter: IntellijFormatter

    @JvmStatic
    @BeforeAll
    fun setUp() {
      formatter = IntellijFormatter(Paths.get("."), NoopFormattingEvents.INSTANCE)
    }
  }

  @AfterEach
  fun tearDown() {
    mdTable?.print()
  }

  @Test
  fun testRootSettings() {
    // given:
    val snapshot = readSnapshot("snapshots/rootsettings.md.snapshot")
    val codeStyleSettings = someCodeSettings()

    // when:
    val ecCodeStyleSettingsAccessors = ECCodeStyleSettingsAccessors(codeStyleSettings)

    // then:
    mdTable = MdTable(
      cols,
      widths,
      ecCodeStyleSettingsAccessors.rootSettingsDescriptor(),
    )

    assertThat(mdTable?.text).isEqualTo(snapshot)
  }

  @Test
  fun testCommonSettings() {
    // given:
    val snapshot = readSnapshot("snapshots/commonsettings.md.snapshot")
    val codeStyleSettings = someCodeSettings()

    // when:
    val ecCodeStyleSettingsAccessors = ECCodeStyleSettingsAccessors(codeStyleSettings)

    // then:
    mdTable = MdTable(
      cols,
      widths,
      ecCodeStyleSettingsAccessors.commonSettingsDescriptors(),
    )

    assertThat(mdTable?.text).isEqualTo(snapshot)
  }

  @Test
  fun testCustomJavaSettings() {
    // given:
    val snapshot = readSnapshot("snapshots/customjavasettings.md.snapshot")
    val codeStyleSettings = someCodeSettings()

    // when:
    val ecCodeStyleSettingsAccessors = ECCodeStyleSettingsAccessors(codeStyleSettings)

    // then:
    mdTable = MdTable(
      cols,
      widths,
      ecCodeStyleSettingsAccessors.languageSettingsDescriptor("java"),
    )

    assertThat(mdTable?.text).isEqualTo(snapshot)
  }

  @Test
  fun testCustomXmlSettings() {
    // given:
    val snapshot = readSnapshot("snapshots/customxmlsettings.md.snapshot")
    val codeStyleSettings = someCodeSettings()

    // when:
    val ecCodeStyleSettingsAccessors = ECCodeStyleSettingsAccessors(codeStyleSettings)

    // then:
    mdTable = MdTable(
      cols,
      widths,
      ecCodeStyleSettingsAccessors.languageSettingsDescriptor("xml"),
    )

    assertThat(mdTable?.text).isEqualTo(snapshot)
  }
}

class MdTable(
  cols: List<String>,
  widths: List<Int>,
  allProperties: List<CodeStyleSettingDescriptor>
) {

  private val stringBuilder: StringBuilder = StringBuilder(256)

  init {
    row(cols, widths)
    row(listOf("", "", ""), widths, '-')

    allProperties.stream()
      .map {
        listOf(
          it.name,
          it.options.joinToString(separator = ", "),
          it.exampleValue
        )
      }
      .forEach { row -> row(row, widths) }
  }

  fun print() {
    println(stringBuilder)
  }

  val text: String
    get() = stringBuilder.toString()

  private fun row(values: List<String>, cols: List<Int>, padChar: Char = ' ') {
    val row = values.mapIndexed { i, value -> value.padEnd(cols[i], padChar) }
      .joinToString(separator = " | ", prefix = "| ", postfix = " |")

    stringBuilder.append(row).append('\n')
  }
}

private fun readSnapshot(resourcePath: String) = String(
  ECCodeStyleSettingsAccessors::class.java.classLoader
    .getResourceAsStream(resourcePath)!!
    .readAllBytes()
)
