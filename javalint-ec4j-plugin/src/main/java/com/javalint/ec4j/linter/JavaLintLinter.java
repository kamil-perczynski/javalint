package com.javalint.ec4j.linter;

import com.javalint.ec.settings.ECCodeStyle;
import com.javalint.ec.settings.ParsedECProperties;
import com.javalint.formatter.IntellijFormatter;
import com.javalint.formatter.IntellijFormatterOptions;
import org.ec4j.core.ResourceProperties;
import org.ec4j.lint.api.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JavaLintLinter implements Linter {

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
                      ViolationHandler violationHandler) {
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
    if (resource.getText() == null) {
      throw new IllegalStateException("Impossible?");
    }
  }

  private static Path projectRootPath() {
    return Paths.get(".").toAbsolutePath().normalize();
  }

}
