package io.github.kamilperczynski.javalint.formatter.ec

import org.ec4j.core.Cache
import org.ec4j.core.EditorConfigLoader
import org.ec4j.core.Resource
import org.ec4j.core.ResourcePath
import org.ec4j.core.ResourcePropertiesService
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class ECFile(
  private val projectRoot: Path,
  ecFileName: String = ".editorconfig"
) : ECSource {

  private var ecPropsService = createResourcePropertiesService(projectRoot, ecFileName)

  override fun findECProps(file: Path): Set<ECProperty> {
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

private fun createResourcePropertiesService(
  projectRoot: Path,
  ecFileName: String
): ResourcePropertiesService {
  return ResourcePropertiesService.builder()
    .cache(Cache.Caches.permanent())
    .loader(EditorConfigLoader.default_())
    .configFileName(ecFileName)
    .rootDirectory(
      ResourcePath.ResourcePaths.ofPath(projectRoot, StandardCharsets.UTF_8)
    )
    .build()
}

