package io.github.kamilperczynski.javalint.formatter.ec

import java.nio.file.Path

interface ECSource {
  fun findECProps(file: Path): List<ECProperty>
  fun charset(file: Path): String
}
