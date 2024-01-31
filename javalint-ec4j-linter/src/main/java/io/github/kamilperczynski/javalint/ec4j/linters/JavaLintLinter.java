package io.github.kamilperczynski.javalint.ec4j.linters;

import io.github.kamilperczynski.javalint.formatter.IntellijFormatter;
import io.github.kamilperczynski.javalint.formatter.IntellijFormatterOptions;
import io.github.kamilperczynski.javalint.formatter.NoopFormattingEvents;
import io.github.kamilperczynski.javalint.formatter.ec.ECCodeStyle;
import io.github.kamilperczynski.javalint.formatter.ec.ParsedECProperties;
import org.ec4j.core.ResourceProperties;
import org.ec4j.lint.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JavaLintLinter implements Linter {

  private static final Logger log = LoggerFactory.getLogger(JavaLintLinter.class);
  private static final int _4MB = 4_000_000;

  private final IntellijFormatter formatter;

  public JavaLintLinter() {
    this.formatter = new IntellijFormatter(
      new IntellijFormatterOptions(projectRootPath(), NoopFormattingEvents.INSTANCE)
    );
  }

  @Override
  public List<String> getDefaultExcludes() {
    return List.of(
      "target",
      "**/target/**",
      "node_modules",
      "**/node_modules/**",
      ".git",
      ".git/**",
      ".gitignore"
    );
  }

  @Override
  public List<String> getDefaultIncludes() {
    return List.of("**/*");
  }

  @Override
  public void process(Resource resource,
                      ResourceProperties resourceProperties,
                      ViolationHandler violationHandler) throws IOException {
    if (Files.size(resource.getPath()) > _4MB) {
      log.info("File {} size is over 4MB - ignoring", resource);
      return;
    }

    ensureResourceIsRead(resource);

    final ECCodeStyle javaLintCodeStyle = new ECCodeStyle(
      new ParsedECProperties(resourceProperties)
    );

    formatter.formatFile(resource.getPath(), javaLintCodeStyle, (path, psiElement) -> {
      final Violation violation = new Violation(
        resource,
        new Location(1, resource.length()),
        new ReplaceFileContentEdit(path, new String(psiElement.textToCharArray())),
        this,
        "code-style"
      );

      violationHandler.handle(violation);
      return null;
    });
  }

  private static void ensureResourceIsRead(Resource resource) {
    // Resource implements lazy file content loading
    // To ensure proper working of the plugin, it requires
    // reading the file regardless if we use Resource object or not
    if (resource.getTextAsCharSequence() == null) {
      throw new IllegalStateException("Impossible?");
    }
  }

  private static Path projectRootPath() {
    return Paths.get(".").toAbsolutePath().normalize();
  }

}
