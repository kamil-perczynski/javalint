package com.javalint.formatter

import java.nio.file.Path

enum class TestFormatterEvents : FormatterEvents {
  INSTANCE;

  override fun formattingStarted() {
  }

  override fun fileFormattingStarted(path: Path) {
  }

  override fun fileIgnored(path: Path) {
  }

  override fun fileFormattingEnd(path: Path, isModified: Boolean) {
  }

  override fun formattingEnd() {
  }

}
