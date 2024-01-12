package io.github.kamilperczynski.javalint.formatter

import java.nio.file.Path

enum class NoopFormattingEvents : FormatterEvents {

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
