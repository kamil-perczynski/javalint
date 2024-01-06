package com.javalint

import com.javalint.cli.executeCli
import org.junit.Assert.assertEquals
import org.junit.Test


class MainKtTest {

  @Test
  fun testCheckFormattingWithCustomPatterns() {
    val exitCode = executeCli(
      ".mvn/**", "src/**", "!target", "--cwd", "/Users/kperczynski/IdeaProjects/augias"
    )
    assertEquals(1, exitCode)
  }

  @Test
  fun testCheckFormattingWithDefaultSettings() {
    val exitCode = executeCli("--cwd", "/Users/kperczynski/IdeaProjects/augias")
    assertEquals(0, exitCode)
  }

  @Test
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
