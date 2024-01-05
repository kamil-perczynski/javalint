package com.javalint.ec.settings

import org.ec4j.core.ResourceProperties
import org.ec4j.core.model.Property
import java.nio.file.Path
import java.util.stream.Collectors

class ParsedECProperties(private val resourceProperties: ResourceProperties) : ECSource {

  override fun findECProps(file: Path): List<ECProperty> {
    return toEditorConfigCodeStyle(resourceProperties)
  }

}

fun toEditorConfigCodeStyle(resourceProperties: ResourceProperties): List<ECProperty> {
  val properties = resourceProperties.properties

  return properties.values.stream()
    .map(::toEditorConfigProperty)
    .collect(Collectors.toList())
}

private fun toEditorConfigProperty(property: Property): ECProperty {
  return ECProperty(property.name, property.sourceValue)
}
