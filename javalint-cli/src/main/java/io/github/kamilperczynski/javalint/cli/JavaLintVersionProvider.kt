package io.github.kamilperczynski.javalint.cli

import picocli.CommandLine
import java.util.*

private const val POM_PROPERTIES_PATH =
  "META-INF/maven/io.github.kamilperczynski.javalint/javalint-cli/pom.properties"
private const val LOCAL_POM_PROPERTIES_PATH = "pom.properties"

class JavaLintVersionProvider : CommandLine.IVersionProvider {

  override fun getVersion(): Array<String> {
    val inStream = javaClass.classLoader.getResourceAsStream(POM_PROPERTIES_PATH)
      ?: javaClass.classLoader.getResourceAsStream(LOCAL_POM_PROPERTIES_PATH)

    inStream.use {
      val pomProperties = Properties()
      pomProperties.load(it)

      return arrayOf(pomProperties.getProperty("version"))
    }
  }

}
