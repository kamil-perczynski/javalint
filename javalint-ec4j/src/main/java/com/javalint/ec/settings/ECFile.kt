package com.javalint.ec.settings

import org.ec4j.core.*
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class ECFile(private val projectRoot: Path) : ECSource {

  private var ecPropsService = createResourcePropertiesService(projectRoot)

  override fun findECProps(file: Path): List<ECProperty> {
    val fullFilePath = projectRoot.resolve(file)

    val resourceProperties = ecPropsService.queryProperties(
      Resource.Resources.ofPath(fullFilePath, StandardCharsets.UTF_8)
    )

    return toEditorConfigCodeStyle(resourceProperties)
  }

  override fun charset(file: Path): String {
    val fullFilePath = projectRoot.resolve(file)

    val ecFileProps = ecPropsService.queryProperties(
      Resource.Resources.ofPath(fullFilePath, StandardCharsets.UTF_8)
    )

    return findCharsetProperty(ecFileProps)
  }

}

private fun createResourcePropertiesService(projectRoot: Path): ResourcePropertiesService {
  return ResourcePropertiesService.builder()
    .cache(Cache.Caches.permanent())
    .loader(EditorConfigLoader.default_())
    .rootDirectory(
      ResourcePath.ResourcePaths.ofPath(projectRoot, StandardCharsets.UTF_8)
    )
    .build()
}

