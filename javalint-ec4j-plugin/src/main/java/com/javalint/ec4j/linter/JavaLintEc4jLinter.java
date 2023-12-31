package com.javalint.ec4j.linter;

import com.javalint.codestyle.InlineJavaLintCodeStyle;
import com.javalint.formatter.IntellijFormatter;
import com.javalint.formatter.IntellijFormatterOptions;
import org.ec4j.core.ResourceProperties;
import org.ec4j.lint.api.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JavaLintEc4jLinter implements Linter {

  public static final InlineJavaLintCodeStyle JAVA_LINT_CODE_STYLE = new InlineJavaLintCodeStyle();

  private final IntellijFormatter formatter;

  public JavaLintEc4jLinter() {
    this.formatter = new IntellijFormatter(
      new IntellijFormatterOptions(
        projectRootPath(),
        NoopFormattingEvents.INSTANCE
      )
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
                      ViolationHandler violationHandler) {
    ensureResourceIsRead(resource);

    formatter.formatFiles(
      List.of(resource.getPath()),
      JAVA_LINT_CODE_STYLE,
      (path, psiElement) -> {
        final Violation violation = new Violation(
          resource,
          new Location(1, resource.length()),
          new ReplaceFileContentEdit(path, new String(psiElement.textToCharArray())),
          this,
          "code-style"
        );

        violationHandler.handle(violation);
        return null;
      }
    );
  }

  private static void ensureResourceIsRead(Resource resource) {
    // Resource implements lazy file content loading
    // To ensure proper working of the plugin, it requires
    // reading the file regardless if we use Resource object or not
    if (resource.getText() == null) {
      throw new IllegalStateException("Impossible?");
    }
  }

  private static Path projectRootPath() {
    return Paths.get(".").toAbsolutePath().normalize();
  }

}
