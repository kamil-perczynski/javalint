package com.intellij.psi.impl;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

/**
 * This file has been modified from its original version.
 * Modifications made by Kamil Perczyński on 2024-01-14
 * <p>
 * Modifications:
 * <ul>
 * <li>Removed check for synchronization between Document and PsiFile</li>
 * </ul>
 */
public class LocalImpl extends PsiDocumentManagerBase {

  protected LocalImpl(@NotNull Project project) {
    super(project);
  }

  @Override
  public Document getDocument(@NotNull PsiFile file) {
    Document document = getCachedDocument(file);
    if (document != null) {
      if (!file.getViewProvider().isPhysical()) {
        PsiUtilCore.ensureValid(file);
        associatePsi(document, file);
      }
      return document;
    }

    FileViewProvider viewProvider = file.getViewProvider();
    if (!viewProvider.isEventSystemEnabled()) {
      return null;
    }

    VirtualFile virtualFile = viewProvider.getVirtualFile();
    document = FileDocumentManager.getInstance().getDocument(virtualFile, myProject);
    if (document != null) {
      if (!viewProvider.isPhysical()) {
        PsiUtilCore.ensureValid(file);
        associatePsi(document, file);
        file.putUserData(Key.create("HARD_REFERENCE_TO_DOCUMENT"), document);
      }
    }

    return document;
  }

}
