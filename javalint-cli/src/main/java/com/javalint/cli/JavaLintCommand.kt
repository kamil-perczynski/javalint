package com.javalint.cli


import com.javalint.cli.commands.CheckFormattingCommand
import com.javalint.cli.commands.FixFormattingCommand
import com.javalint.cli.gitignore.*
import com.javalint.codestyle.InlineJavaLintCodeStyle
import picocli.CommandLine.*
import java.io.PrintStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Callable
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors.toList

val patternTypesPriority: List<JavaLintPathPattern.Type> = listOf(
  JavaLintPathPattern.Type.IGNORES,
  JavaLintPathPattern.Type.INCLUDES
)

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
  # Prepend ! to negate the pattern, KtLint uses .gitignore pattern style syntax.
  # Globs are applied starting from the last one.
  #
  # Hidden folders will be skipped.
  # Check all '.kt' files in 'src/' directory, but ignore files ending with 'Test.kt':
  javalint "src/**/*.kt" "!src/**/*Test.kt"
  # Check all '.kt' files in 'src/' directory, but ignore 'generated' directory and its subdirectories:
  javalint "src/**/*.kt" "!src/**/generated/**"

  # Auto-correct style violations.
  javalint -F "src/**/*.kt"

  # Using custom reporter jar and overriding report location
  javalint --reporter=csv,artifact=/path/to/reporter/csv.jar,output=my-custom-report.csv
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
  var cwd: String = "."

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
        "(e.g. dir/file.kt instead of /home/user/project/dir/file.kt)",
    ],
  )
  var relative: Boolean = false

  @Option(
    names = ["--stdin"],
    description = ["Read file from stdin"],
  )
  private var stdin: Boolean = false

  @Option(
    names = ["--patterns-from-stdin"],
    description = [
      "Read additional patterns to check/format from stdin. " +
        "Patterns are delimited by the given argument. (default is newline) " +
        "If the argument is an empty string, the NUL byte is used.",
    ],
    arity = "0..1",
    fallbackValue = "\n",
  )
  private var stdinDelimiter: String? = null

  @Option(
    names = ["--editorconfig"],
    description = [
      "Path to the default '.editorconfig'. A property value from this file is used only when no " +
        "'.editorconfig' file on the path to the source file specifies that property. Note: up until ktlint " +
        "0.46 the property value in this file used to override values found in '.editorconfig' files on the " +
        "path to the source file.",
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

  private val tripped = AtomicBoolean()
  private val fileNumber = AtomicInteger()
  private val errorNumber = AtomicInteger()
  private val adviseToUseFormat = AtomicBoolean()

  override fun call(): Int {
    val projectRoot = Paths.get(cwd).toAbsolutePath().normalize()

    val pathsFilter: PathsFilter = toPathsFilter(projectRoot, patterns)
    val codeStyle = InlineJavaLintCodeStyle()
    val paths = discoverProjectFiles(projectRoot, pathsFilter)

    if (format) {
      return FixFormattingCommand(projectRoot, paths, codeStyle).call()
    }

    return CheckFormattingCommand(projectRoot, paths, codeStyle).call()
  }

}

private fun toPathsFilter(projectRoot: Path, cliPatterns: List<String>): PathsFilter {
  if (cliPatterns.isEmpty()) {
    return defaultPathsFilter
  }

  val javaLintPatterns = JavaLintPathPatterns(
    cliPatterns.stream()
      .map(::parseCliJavaLintPathPattern)
      .sorted(Comparator.comparing { patternTypesPriority.indexOf(it.type) })
      .collect(toList())
  )
  return JavaLintPatternPathFilter(projectRoot, javaLintPatterns)
}
