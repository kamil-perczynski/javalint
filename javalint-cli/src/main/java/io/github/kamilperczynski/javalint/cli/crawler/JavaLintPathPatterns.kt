package io.github.kamilperczynski.javalint.cli.crawler

import java.nio.file.Path

data class JavaLintPathPatterns(val patterns: List<JavaLintPathPattern>) {

  fun matches(path: Path): JavaLintPathPattern? {
    return patterns.stream()
      .filter { it.matches(path) }
      .findFirst()
      .orElse(null)
  }

}