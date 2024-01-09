package com.javalint

import com.javalint.cli.executeCli
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class MainKtTest {

  @Test
  @Disabled
  fun testCheckFormattingWithCustomPatterns() {
    val exitCode = executeCli(
      ".mvn/**", "src/**", "!target", "--cwd", "/Users/kperczynski/IdeaProjects/augias"
    )
    assertEquals(1, exitCode)
  }

  @Test
  @Disabled
  fun testCheckFormattingWithDefaultSettings() {
    val exitCode = executeCli("--cwd", "/Users/kperczynski/IdeaProjects/augias")
    assertEquals(0, exitCode)
  }

  @Test
  @Disabled
  fun testFixFormatting() {
    val exitCode = executeCli("-F", "--cwd", "/Users/kperczynski/IdeaProjects/augias")
    assertEquals(0, exitCode)
  }

  @Test
  fun testPrintVersion() {
    val exitCode = executeCli("--version")
    assertEquals(0, exitCode)
  }

  @Test
  fun testPrintUsageInfo() {
    val exitCode = executeCli("--help")
    assertEquals(0, exitCode)
  }

}
