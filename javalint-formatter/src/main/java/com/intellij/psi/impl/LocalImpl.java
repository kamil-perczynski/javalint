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
//      if (document.getTextLength() != file.getTextLength()) {
//        // We have internal state inconsistency, it might be a good idea to contact the core team if you are able to reproduce this error.
//        String message = "Document/PSI mismatch: " + file + " of " + file.getClass() +
//          "; viewProvider=" + viewProvider +
//          "; uncommitted=" + Arrays.toString(getUncommittedDocuments());
//
//        System.out.println("WARN: " + message);
//      }

      if (!viewProvider.isPhysical()) {
        PsiUtilCore.ensureValid(file);
        associatePsi(document, file);
        file.putUserData(Key.create("HARD_REFERENCE_TO_DOCUMENT"), document);
      }
    }

    return document;
  }
}
