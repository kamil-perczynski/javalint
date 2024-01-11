package com.javalint.cli.commands

import com.intellij.util.io.toByteArray
import com.javalint.codestyle.JavaLintCodeStyle
import com.javalint.formatter.IntellijFormatter
import com.javalint.formatter.IntellijFormatterOptions
import com.javalint.formatter.output.FixFormattingCommandEvents
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
