@file:Suppress("UnstableApiUsage")

package com.javalint.formatter.internal

import com.intellij.formatting.service.FormattingService
import com.intellij.lang.LanguageFormattingRestriction
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.openapi.editor.impl.DocumentWriteAccessGuard
import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.extensions.ExtensionsArea
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiElementFinder
import com.intellij.psi.PsiTreeChangeListener
import com.intellij.psi.augment.PsiAugmentProvider
import com.intellij.psi.codeStyle.*
import com.intellij.psi.impl.PsiElementFinderImpl
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor
import com.intellij.psi.impl.source.codeStyle.PreFormatProcessor
import com.javalint.formatter.lang.FormatterLanguage

fun registerNecessaryExtensions(extensionsArea: ExtensionsArea) {
  extensionsArea.registerExtensionPoint(
    FileTypeIndentOptionsProvider.EP_NAME.name,
    FileTypeIndentOptionsProvider::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )
  extensionsArea.registerExtensionPoint(
    FileIndentOptionsProvider.EP_NAME.name,
    FileIndentOptionsProvider::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )
  extensionsArea.registerExtensionPoint(
    FormattingService.EP_NAME.name,
    FormattingService::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )
  extensionsArea.registerExtensionPoint(
    LanguageCodeStyleSettingsContributor.EP_NAME.name,
    LanguageCodeStyleSettingsContributor::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )
  extensionsArea.registerExtensionPoint(
    LanguageCodeStyleSettingsProvider.EP_NAME.name,
    LanguageCodeStyleSettingsProvider::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )
  extensionsArea.registerExtensionPoint(
    CodeStyleSettingsProvider.EXTENSION_POINT_NAME.name,
    CodeStyleSettingsProvider::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )

  extensionsArea.registerExtensionPoint(
    LanguageFormattingRestriction.EP_NAME.name,
    LanguageFormattingRestriction::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )
  extensionsArea.registerExtensionPoint(
    ExternalFormatProcessor.EP_NAME.name,
    ExternalFormatProcessor::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )
  extensionsArea.registerExtensionPoint(
    PreFormatProcessor.EP_NAME.name,
    PreFormatProcessor::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )
  extensionsArea.registerExtensionPoint(
    PostFormatProcessor.EP_NAME.name,
    PostFormatProcessor::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )
  extensionsArea.registerExtensionPoint(
    PsiAugmentProvider.EP_NAME.name,
    PsiAugmentProvider::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )
  extensionsArea.registerExtensionPoint(
    LanguageInjector.EXTENSION_POINT_NAME.name,
    LanguageInjector::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )
  extensionsArea.registerExtensionPoint(
    DocumentWriteAccessGuard.EP_NAME.name,
    DocumentWriteAccessGuard::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )
}

fun registerNecessaryProjectExtensions(
  extensionsArea: ExtensionsArea,
  formatterLanguages: List<FormatterLanguage>
) {
  extensionsArea.registerExtensionPoint(
    MultiHostInjector.MULTIHOST_INJECTOR_EP_NAME.name,
    MultiHostInjector::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )

  extensionsArea.registerExtensionPoint(
    PsiTreeChangeListener.EP.name,
    PsiTreeChangeListener::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )

  extensionsArea.registerExtensionPoint(
    PsiElementFinder.EP.name,
    PsiElementFinderImpl::class.java.name,
    ExtensionPoint.Kind.BEAN_CLASS,
    false
  )

  for (formatterLanguage in formatterLanguages) {
    formatterLanguage.registerProjectExtensions(extensionsArea)
  }
}
