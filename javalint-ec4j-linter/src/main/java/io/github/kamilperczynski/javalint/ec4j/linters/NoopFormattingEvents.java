package io.github.kamilperczynski.javalint.ec4j.linters;

import io.github.kamilperczynski.javalint.formatter.FormatterEvents;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public enum NoopFormattingEvents implements FormatterEvents {

  INSTANCE;

  @Override
  public void formattingStarted() {
  }

  @Override
  public void fileFormattingStarted(@NotNull Path path) {
  }

  @Override
  public void fileIgnored(@NotNull Path path) {
  }

  @Override
  public void fileFormattingEnd(@NotNull Path path, boolean isModified) {
  }

  @Override
  public void formattingEnd() {
  }

}

