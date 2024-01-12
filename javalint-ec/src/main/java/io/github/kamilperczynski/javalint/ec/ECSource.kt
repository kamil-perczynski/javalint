package io.github.kamilperczynski.javalint.ec

import java.nio.file.Path

interface ECSource {
  fun findECProps(file: Path): List<ECProperty>
  fun charset(file: Path): String
}
