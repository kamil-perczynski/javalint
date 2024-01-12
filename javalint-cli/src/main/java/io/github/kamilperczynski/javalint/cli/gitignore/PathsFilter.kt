package io.github.kamilperczynski.javalint.cli.gitignore

import java.nio.file.Path

interface PathsFilter {

  fun matchDir(dir: Path): Boolean

  fun matchFile(path: Path): Boolean

}
