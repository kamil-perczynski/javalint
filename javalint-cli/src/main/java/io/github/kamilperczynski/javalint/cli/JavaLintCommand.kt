package io.github.kamilperczynski.javalint.cli


import com.intellij.psi.codeStyle.CodeStyleSettings
import io.github.kamilperczynski.javalint.cli.commands.CheckFormattingCommand
import io.github.kamilperczynski.javalint.cli.commands.CheckFormattingCommandEvents
import io.github.kamilperczynski.javalint.cli.commands.FixFormattingCommand
import io.github.kamilperczynski.javalint.cli.commands.FixFormattingCommandEvents
import io.github.kamilperczynski.javalint.cli.crawler.*
import io.github.kamilperczynski.javalint.formatter.IntellijFormatterOptions
import io.github.kamilperczynski.javalint.formatter.codestyle.JavaLintCodeStyle
import io.github.kamilperczynski.javalint.formatter.ec.ECCodeStyle
import io.github.kamilperczynski.javalint.formatter.ec.ECFile
import picocli.CommandLine.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Callable
import java.util.stream.Collectors.toList

/**
 * By default, hidden files and directories are ignored (being compliant to ktlint).
 *
 * Additionally, as reasonable defaults, typical folders with dependencies
 * or build outputs are excluded as well.
 */
val defaultPathsFilter = ExcludeHiddenDirectoriesFilter(
  listOf("target", "build", "dist", "node_modules")
)

@Command(
  headerHeading =
  """
An anti-bikeshedding Java linter with built-in formatter.

Usage:
  javalint <flags> [patterns]
  java -jar javalint.jar <flags> [patterns]

Examples:
  # Check the style of all Java, XML, JSON and Yaml files
  # (ending with '.java', '.xml', '.json', '.yml' or '.yaml') inside the current dir (recursively).
  #
  # Hidden folders will be skipped.
  javalint

  # Check only certain locations starting from the current directory.
  #
  # Prepend ! to negate the pattern, JavaLint uses .gitignore pattern style syntax.
  # Globs are applied starting from the last one.
  #
  # Hidden folders will be skipped.
  # Check all '.java' files in 'src/' directory, but ignore files ending with 'Test.java':
  javalint "src/**/*.java" "!src/**/*Test.java"
  # Check all '.java' files in 'src/' directory, but ignore 'generated' directory and its subdirectories:
  javalint "src/**/*.java" "!src/**/generated/**"

  # Auto-correct style violations.
  javalint -F "src/**/*.java"
Flags:
""",
  synopsisHeading = "",
  customSynopsis = [""],
  sortOptions = false,
  mixinStandardHelpOptions = true,
  versionProvider = JavaLintVersionProvider::class
)
class JavaLintCommand : Callable<Int> {
  @Spec
  private lateinit var commandSpec: Model.CommandSpec

  @Option(
    names = ["--color"],
    description = ["Make output colorful"],
  )
  var color: Boolean = false

  @Option(
    names = ["--current-working-dir", "--cwd"],
    description = ["Current working directory"],
  )
  var workingDir: String = "."

  @Option(
    names = ["--format", "-F"],
    description = ["Fix deviations from the code style when possible"],
  )
  private var format: Boolean = false

  @Option(
    names = ["--limit"],
    description = ["Maximum number of errors to show (default: show all)"],
  )
  private var limit: Int = -1
    get() = if (field < 0) Int.MAX_VALUE else field

  @Option(
    names = ["--relative"],
    description = [
      "Print files relative to the working directory " +
        "(e.g. dir/file.java instead of /home/user/project/dir/file.java)",
    ],
  )
  var relative: Boolean = true

  @Option(
    names = ["--editorconfig"],
    description = [
      "Path to the default '.editorconfig'. A property value from this file is used only when no " +
        "'.editorconfig' file on the path to the source file specifies that property.",
    ],
  )
  private var editorConfigPath: String? = null

  @Option(
    names = ["--baseline"],
    description = ["Defines a baseline file to check against"],
  )
  private var baselinePath: String = ""

  @Parameters(hidden = true)
  private var patterns = emptyList<String>()

  override fun call(): Int {

    val projectRoot = Paths.get(workingDir).toAbsolutePath().normalize()

    val pathsFilter: PathsFilter = toPathsFilter(projectRoot, patterns)
    val paths = discoverProjectFiles(projectRoot, pathsFilter)

    val javaLintCodeStyle = if (Files.exists(projectRoot.resolve(".editorconfig")))
      ECCodeStyle(ECFile(projectRoot))
    else
      DefaultIjCodeStyle.INSTANCE

    if (format) {
      val formatterEvents = FixFormattingCommandEvents(projectRoot)
      val options = IntellijFormatterOptions(projectRoot, formatterEvents)

      return FixFormattingCommand(paths, javaLintCodeStyle, options).call()
    }

    val formatterEvents = CheckFormattingCommandEvents(projectRoot)
    val options = IntellijFormatterOptions(projectRoot, formatterEvents)
    return CheckFormattingCommand(paths, javaLintCodeStyle, options).call()
  }

}

private fun toPathsFilter(projectRoot: Path, cliPatterns: List<String>): PathsFilter {
  if (cliPatterns.isEmpty()) {
    return defaultPathsFilter
  }

  val javaLintPatterns = JavaLintPathPatterns(
    cliPatterns.stream()
      .map(::parseCliJavaLintPathPattern)
      .collect(toList())
      .reversed()
  )
  return JavaLintPatternPathFilter(projectRoot, javaLintPatterns)
}

enum class DefaultIjCodeStyle : JavaLintCodeStyle {
  INSTANCE;

  override fun configure(file: Path, settings: CodeStyleSettings): CodeStyleSettings = settings
}
