package com.javalint.ec.settings

import org.ec4j.core.*
import org.ec4j.core.model.Version
import org.ec4j.core.parser.EditorConfigModelHandler
import org.ec4j.core.parser.EditorConfigParser
import org.ec4j.core.parser.ErrorHandler
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class ECFile(private val projectRoot: Path) : ECSource {

  override fun findECProps(file: Path): List<ECProperty> {
    val resourcePropertiesService: ResourcePropertiesService =
      createResourcePropertiesService(projectRoot)
    val fullFilePath = projectRoot.resolve(file)

    val resourceProperties = resourcePropertiesService.queryProperties(
      Resource.Resources.ofPath(fullFilePath, StandardCharsets.UTF_8)
    )

    return toEditorConfigCodeStyle(resourceProperties)
  }

}

private fun createResourcePropertiesService(projectRoot: Path): ResourcePropertiesService {
  val configsCache = Cache.Caches.permanent()
  val editorConfigParser = EditorConfigParser.builder().build()


  val editorConfigModelHandler =
    EditorConfigModelHandler(PropertyTypeRegistry.default_(), Version.CURRENT)

  editorConfigParser.parse(
    Resource.Resources.ofPath(projectRoot.resolve(".editorconfig"), StandardCharsets.UTF_8),
    editorConfigModelHandler,
    ErrorHandler.THROW_SYNTAX_ERRORS_IGNORE_OTHERS
  )

  return ResourcePropertiesService.builder()
    .cache(configsCache)
    .loader(EditorConfigLoader.default_())
    .rootDirectory(
      ResourcePath.ResourcePaths.ofPath(projectRoot, StandardCharsets.UTF_8)
    )
    .build()
}

