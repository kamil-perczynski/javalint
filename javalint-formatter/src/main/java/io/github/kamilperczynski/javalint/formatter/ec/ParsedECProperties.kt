package io.github.kamilperczynski.javalint.formatter.ec

import org.ec4j.core.ResourceProperties
import org.ec4j.core.model.Property
import org.ec4j.core.model.PropertyType
import java.nio.file.Path
import java.util.stream.Collectors.toSet

class ParsedECProperties(private val resourceProperties: ResourceProperties) : ECSource {

  override fun findECProps(file: Path): Set<ECProperty> {
    return toEditorConfigCodeStyle(resourceProperties)
  }

  override fun charset(file: Path): String {
    return resourceProperties.getValue(PropertyType.charset, "utf-8", true)
  }

}

fun findCharsetProperty(ecFileProps: ResourceProperties): String =
  ecFileProps.getValue(PropertyType.charset, "utf-8", true)

fun toEditorConfigCodeStyle(resourceProperties: ResourceProperties): Set<ECProperty> {
  val properties = resourceProperties.properties

  return properties.values.stream()
    .map(::toEditorConfigProperty)
    .collect(toSet())
}

private fun toEditorConfigProperty(property: Property): ECProperty {
  return ECProperty(property.name, property.sourceValue)
}
