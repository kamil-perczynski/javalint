package io.github.kamilperczynski.javalint.cli

import picocli.CommandLine
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  val exitStatus = executeCli(*args)
  exitProcess(exitStatus)
}

fun executeCli(vararg args: String): Int {
  val javaLintCommand = JavaLintCommand()
  val commandLine = CommandLine(javaLintCommand)

  val parsedArgs = commandLine.parseArgs(*args)

  if (commandLine.isUsageHelpRequested) {
    commandLine.usage(System.out, CommandLine.Help.Ansi.ON)
    return 0
  } else if (commandLine.isVersionHelpRequested) {
    commandLine.printVersionHelp(System.out, CommandLine.Help.Ansi.ON)
    return 0
  }

  if (parsedArgs.hasSubcommand()) {
    commandLine.usage(System.out, CommandLine.Help.Ansi.ON)
    return 0
  }

  return javaLintCommand.call()
}

