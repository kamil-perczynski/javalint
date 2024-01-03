package com.javalint.ec4j.linter;

import com.javalint.ec4j.linter.settings.EditorConfigJavaLintCodeStyle;
import com.javalint.ec4j.linter.settings.EditorConfigProperty;
import com.javalint.formatter.IntellijFormatter;
import com.javalint.formatter.IntellijFormatterOptions;
import org.ec4j.core.ResourceProperties;
import org.ec4j.core.model.Property;
import org.ec4j.lint.api.*;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    final EditorConfigJavaLintCodeStyle javaLintCodeStyle =
      toEditorconfigJavaLintCodeStyle(resourceProperties);

    formatter.formatFiles(List.of(resource.getPath()), javaLintCodeStyle, (path, psiElement) -> {
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

  @NotNull
  private static EditorConfigJavaLintCodeStyle toEditorconfigJavaLintCodeStyle(ResourceProperties resourceProperties) {
    final Map<String, Property> properties = resourceProperties.getProperties();

    final List<EditorConfigProperty> editorconfigProperties = properties.values().stream()
      .map(JavaLintLinter::toEditorConfigProperty)
      .collect(Collectors.toList());

    return new EditorConfigJavaLintCodeStyle(editorconfigProperties);
  }

  @NotNull
  private static EditorConfigProperty toEditorConfigProperty(Property property) {
    return new EditorConfigProperty(property.getName(), property.getSourceValue());
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
