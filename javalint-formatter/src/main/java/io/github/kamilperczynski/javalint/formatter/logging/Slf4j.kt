package io.github.kamilperczynski.javalint.formatter.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class Slf4j {
  val log: Logger = LoggerFactory.getLogger(this::class.java.declaringClass)
}
