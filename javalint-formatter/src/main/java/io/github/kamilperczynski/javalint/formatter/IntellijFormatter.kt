@file:Suppress("UnstableApiUsage")

package io.github.kamilperczynski.javalint.formatter

import com.intellij.application.options.codeStyle.cache.CodeStyleCachingService
import com.intellij.application.options.codeStyle.cache.CodeStyleCachingServiceImpl
import com.intellij.core.CoreApplicationEnvironment
import com.intellij.core.CoreFileTypeRegistry
import com.intellij.core.JavaCoreProjectEnvironment
import com.intellij.formatting.Formatter
import com.intellij.formatting.FormatterImpl
import com.intellij.formatting.service.CoreFormattingService
import com.intellij.formatting.service.FormattingService
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.mock.MockProject
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.application.TransactionGuard
import com.intellij.openapi.application.TransactionGuardImpl
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.impl.EditorFactoryImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.local.CoreLocalVirtualFile
import com.intellij.pom.PomModel
import com.intellij.pom.core.impl.LangPomModel
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.*
import com.intellij.psi.codeStyle.*
import com.intellij.psi.impl.LocalImpl
import com.intellij.psi.impl.PsiDocumentManagerBase
import com.intellij.psi.impl.PsiManagerImpl
import com.intellij.psi.impl.PsiSubstitutorFactoryImpl
import com.intellij.psi.impl.source.PostprocessReformattingAspect
import com.intellij.psi.impl.source.PostprocessReformattingAspectImpl
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageManagerImpl
import com.intellij.psi.util.ReadActionCache
import com.intellij.psi.util.ReadActionCacheImpl
import io.github.kamilperczynski.javalint.formatter.codestyle.JavaLintCodeStyle
import io.github.kamilperczynski.javalint.formatter.internal.WriteableCoreLocalVirtualFile
import io.github.kamilperczynski.javalint.formatter.internal.registerNecessaryExtensions
import io.github.kamilperczynski.javalint.formatter.internal.registerNecessaryProjectExtensions
import io.github.kamilperczynski.javalint.formatter.lang.*
import java.awt.EventQueue
import java.nio.file.Path

class IntellijFormatter(
  private val homePath: Path,
  private val formatterEvents: FormatterEvents
) {

  private val projectCodeStyleSettingsManager: ProjectCodeStyleSettingsManager
  private val virtualFileManager: VirtualFileManager
  private val codeStyleManager: CodeStyleManager
  private val psiManager: PsiManager
  private val project: Project

  private val formatterLanguages: List<FormatterLanguage> = listOf(
    JavaFormatterLanguage(),
    XmlFormatterLanguage(),
    JsonFormatterLanguage(),
    YamlFormatterLanguage()
  )

  init {
    System.setProperty("ide.hide.excluded.files", "false")
    System.setProperty("psi.sleep.in.validity.check", "false")
    System.setProperty("psi.incremental.reparse.depth.limit", "1000")
    System.setProperty("java.formatter.chained.calls.pre212.compatibility", "false")
    System.setProperty("platform.random.idempotence.check.rate", "1000")

    setStubbedHomePath(homePath)

    val applicationEnv = CoreApplicationEnvironment(Disposer.newDisposable())

    registerNecessaryServices(applicationEnv)
    registerNecessaryExtensions(applicationEnv.application.extensionArea)
    registerLanguageComponents(applicationEnv.application, formatterLanguages)

    val projectEnv = JavaCoreProjectEnvironment(applicationEnv.application, applicationEnv)

    registerNecessaryProjectExtensions(projectEnv.project.extensionArea, formatterLanguages)
    val projectComponents = registerProjectComponents(projectEnv.project, formatterLanguages)

    val fileTypeRegistry: CoreFileTypeRegistry = CoreFileTypeRegistry.getInstance()
      as CoreFileTypeRegistry

    for (formatterLanguage in formatterLanguages) {
      formatterLanguage.registerFileType(fileTypeRegistry)
    }

    this.project = projectEnv.project
    this.psiManager = PsiManagerImpl.getInstance(projectEnv.project)
    this.codeStyleManager = projectComponents.codeStyleManagerImpl
    this.virtualFileManager = VirtualFileManager.getInstance()
    this.projectCodeStyleSettingsManager = projectComponents.projectCodeStyleSettingsManager
  }

  fun formatFile(
    filePath: Path,
    javaLintCodeStyle: JavaLintCodeStyle,
    onFileFormatted: (path: Path, formattedElement: PsiElement) -> Unit
  ) {
    val configuredCodeStyle = javaLintCodeStyle.configure(filePath, this::createCodeStyleSettings)

    EventQueue.invokeAndWait {
      WriteCommandAction.runWriteCommandAction(project) {
        projectCodeStyleSettingsManager.runWithLocalSettings(
          configuredCodeStyle,
          Runnable {
            try {
              formatPath(filePath, onFileFormatted)
            } catch (e: Exception) {
              throw JavalintFormatterException("Formatting failure at file: $filePath", e)
            }
          }
        )
      }
    }
  }

  private fun createCodeStyleSettings(): CodeStyleSettings {
    val codeStyleSettings = projectCodeStyleSettingsManager.createSettings()

    for (formatterLanguage in formatterLanguages) {
      formatterLanguage.configureCodeStyleSettings(codeStyleSettings)
    }

    return codeStyleSettings
  }

  private fun formatPath(
    relativeFile: Path,
    onFileFormatted: (path: Path, formattedElement: PsiElement) -> Unit
  ) {
    val psiFile = toPsiFile(relativeFile)

    if (psiFile == null) {
      formatterEvents.fileIgnored(relativeFile)
      return
    }

    formatterEvents.fileFormattingStarted(relativeFile)

    val originalContent = psiFile.textToCharArray()
    val reformattedElement = codeStyleManager.reformat(psiFile)

    val isModified = !psiFile.textToCharArray().contentEquals(originalContent)

    formatterEvents.fileFormattingEnd(relativeFile, isModified)

    if (isModified) {
      onFileFormatted.invoke(relativeFile, reformattedElement)
    }
  }

  private fun toPsiFile(relativeFile: Path): PsiFile? {
    val absoluteFilePath = homePath.resolve(relativeFile)
    val virtualFile = toLocalVirtualFile(virtualFileManager, absoluteFilePath.toString())

    if (virtualFile.fileType.isBinary) {
      return null
    }

    return psiManager.findFile(
      WriteableCoreLocalVirtualFile(virtualFile)
    )
  }

}


private fun toLocalVirtualFile(fsManager: VirtualFileManager, path: String): CoreLocalVirtualFile {
  return fsManager.getFileSystem("file").findFileByPath(path) as CoreLocalVirtualFile
}

private fun registerLanguageComponents(
  application: Application,
  formatterLanguages: List<FormatterLanguage>
) {
  for (formatterLanguage in formatterLanguages) {
    formatterLanguage.registerLanguageComponents()
  }

  val extensionPoint = application.extensionArea.getExtensionPoint(FormattingService.EP_NAME)
  extensionPoint.registerExtension(CoreFormattingService(), application)
}

private fun setStubbedHomePath(homePath: Path) {
  val homePathString = homePath.toRealPath().toString()
  System.setProperty(PathManager.PROPERTY_HOME_PATH, homePathString)
}

private fun registerNecessaryServices(applicationEnvironment: CoreApplicationEnvironment) {
  applicationEnvironment.registerApplicationService(
    CodeStyleSettingsService::class.java,
    CodeStyleSettingsServiceImpl()
  )

  applicationEnvironment.registerApplicationService(Formatter::class.java, FormatterImpl())
  applicationEnvironment.registerApplicationService(
    TransactionGuard::class.java,
    TransactionGuardImpl()
  )

  applicationEnvironment.registerApplicationService(
    PsiSubstitutorFactory::class.java,
    PsiSubstitutorFactoryImpl()
  )

  applicationEnvironment.registerApplicationService(
    EditorFactory::class.java,
    EditorFactoryImpl()
  )

  applicationEnvironment.registerApplicationService(
    ReadActionCache::class.java,
    ReadActionCacheImpl()
  )
}

private fun registerProjectComponents(
  project: MockProject,
  formatterLanguages: List<FormatterLanguage>
): FormatterComponents {
  for (formatterLanguage in formatterLanguages) {
    formatterLanguage.registerProjectComponents(project)
  }

  project.registerService(
    InjectedLanguageManager::class.java,
    InjectedLanguageManagerImpl::class.java
  )

  project.registerService(PsiDocumentManager::class.java, LocalImpl::class.java)
  project.registerService(CodeStyleCachingService::class.java, CodeStyleCachingServiceImpl(project))
  project.registerService(TreeAspect::class.java, TreeAspect())
  project.registerService(PomModel::class.java, LangPomModel(project))

  configurePsiDocumentManager(project)

  val projectCodeStyleSettingsManager = ProjectCodeStyleSettingsManager(project)

  project.registerService(
    ProjectCodeStyleSettingsManager::class.java,
    projectCodeStyleSettingsManager
  )

  val codeStyleManagerImpl = CodeStyleManagerImpl(project)
  project.registerService(CodeStyleManager::class.java, codeStyleManagerImpl)
  project.registerService(
    PostprocessReformattingAspect::class.java,
    PostprocessReformattingAspectImpl(project)
  )

  return FormatterComponents(projectCodeStyleSettingsManager, codeStyleManagerImpl)
}

private fun configurePsiDocumentManager(project: Project): PsiDocumentManagerBase {
  val psiDocumentManager = PsiDocumentManager.getInstance(project) as PsiDocumentManagerBase
  psiDocumentManager.synchronizer.isIgnorePsiEvents = true
  return psiDocumentManager
}
