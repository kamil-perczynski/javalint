package com.intellij.pom.core.impl;

import com.intellij.openapi.project.Project;
import com.intellij.pom.PomModelAspect;
import com.intellij.pom.event.PomModelEvent;
import com.intellij.psi.impl.source.PostprocessReformattingAspect;
import com.intellij.psi.impl.source.PostprocessReformattingAspectImpl;
import org.jetbrains.annotations.NotNull;

/**
 * This file has been modified from its original version.
 * Modifications made by Kamil Perczyński on 2024-01-14
 */
public class LangPomModel extends PomModelImpl {

  private final PostprocessReformattingAspect myAspect;

  public LangPomModel(@NotNull Project project) {
    super(project);
    myAspect = new PostprocessReformattingAspectImpl(project);
  }

  @Override
  public <T extends PomModelAspect> T getModelAspect(@NotNull Class<T> aClass) {
    //noinspection unchecked
    return myAspect.getClass().equals(aClass) || myAspect.getClass().getSuperclass().equals(aClass)
      ? (T) myAspect
      : super.getModelAspect(aClass);
  }

  @Override
  protected void updateDependentAspects(PomModelEvent event) {
    super.updateDependentAspects(event);
    myAspect.update(event);
  }

}
