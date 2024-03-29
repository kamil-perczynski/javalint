package com.intellij.psi.util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.ProcessingContext

/**
 * This file has been modified from its original version.
 * Modifications made by Kamil Perczyński on 2024-01-14
 */
class ReadActionCacheImpl : ReadActionCache {

  private val threadProcessingContext: ThreadLocal<ProcessingContext> = ThreadLocal()

  override val processingContext: ProcessingContext?
    get() {
      threadProcessingContext.get()?.let { return it }
      if (ApplicationManager.getApplication().isWriteIntentLockAcquired) return writeActionProcessingContext
      if (!ApplicationManager.getApplication().isReadAccessAllowed) return null
      threadProcessingContext.set(ProcessingContext())
      return threadProcessingContext.get()
    }

  fun clear() {
    threadProcessingContext.remove()
  }

  private var writeActionProcessingContext: ProcessingContext? = null

  override fun <T> allowInWriteAction(supplier: () -> T): T {
    return if (!ApplicationManager.getApplication().isWriteIntentLockAcquired || writeActionProcessingContext != null) {
      supplier.invoke()
    } else try {
      writeActionProcessingContext = ProcessingContext()
      supplier.invoke()
    } finally {
      writeActionProcessingContext = null
    }
  }

  override fun allowInWriteAction(runnable: Runnable) {
    allowInWriteAction(runnable::run)
  }

}

