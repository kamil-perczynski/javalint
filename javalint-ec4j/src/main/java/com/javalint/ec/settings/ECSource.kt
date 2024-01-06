package com.javalint.ec.settings

import java.nio.file.Path

interface ECSource {
  fun findECProps(file: Path): List<ECProperty>
}
