package io.github.kamilperczynski.javalint.cli.crawler

import java.nio.file.Path

interface JavaLintPathPattern {

  val type: Type
  val text: String

  fun matches(path: Path): Boolean

  enum class Type {
    INCLUDES, IGNORES
  }
}
