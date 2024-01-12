package io.github.kamilperczynski.javalint.cli.commands

import com.intellij.util.io.toByteArray
import io.github.kamilperczynski.javalint.formatter.codestyle.JavaLintCodeStyle
import io.github.kamilperczynski.javalint.formatter.IntellijFormatter
import io.github.kamilperczynski.javalint.formatter.IntellijFormatterOptions
import java.nio.CharBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class FixFormattingCommand(
  private val projectRoot: Path,
  private val paths: List<Path>,
  private val codeStyle: JavaLintCodeStyle
) : Callable<Int> {

  override fun call(): Int {
    val formatterEvents = FixFormattingCommandEvents(projectRoot)

    val formatter = IntellijFormatter(
      IntellijFormatterOptions(projectRoot, formatterEvents)
    )

    val threadPool = Executors.newFixedThreadPool(2)

    formatterEvents.formattingStarted()

    for (path in paths) {
      formatter.formatFile(path, codeStyle) { _, el ->
        val charBuffer = CharBuffer.wrap(el.textToCharArray())

        threadPool.submit {
          val byteBuffer = codeStyle.charset(path).encode(charBuffer)

          Files.write(projectRoot.resolve(path), byteBuffer.toByteArray())
        }
      }
    }

    formatterEvents.formattingEnd()

    threadPool.shutdown()
    threadPool.awaitTermination(5, TimeUnit.MINUTES)
    return 0
  }

}
