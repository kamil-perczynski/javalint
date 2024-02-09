package io.github.kamilperczynski.javalint.ec4j.linters;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import io.github.kamilperczynski.javalint.formatter.IntellijFormatter;
import io.github.kamilperczynski.javalint.formatter.NoopFormattingEvents;
import io.github.kamilperczynski.javalint.formatter.ec.ECCodeStyle;
import io.github.kamilperczynski.javalint.formatter.ec.ECSource;
import io.github.kamilperczynski.javalint.formatter.ec.ParsedECProperties;
import org.ec4j.core.ResourceProperties;
import org.ec4j.core.model.Property;
import org.ec4j.lint.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ec4j.lint.api.Constants.DEFAULT_EXCLUDES;

/**
 * {@link Linter} implementation for javalint
 *
 * <p>
 * The implementation tries to reuse created {@link ECSource} as well
 * as {@link CodeStyleSettings}, which makes it <b>not</b> thread safe.
 */
public class JavaLintLinter implements Linter {

  private static final Logger log = LoggerFactory.getLogger(JavaLintLinter.class);
  private static final int _4MB = 4_000_000;

  private final IntellijFormatter formatter;

  private Map<String, Property> lastResourceProperties;
  private ECCodeStyle currentEcCodeStyle;

  public JavaLintLinter() {
    this.formatter = new IntellijFormatter(projectRootPath(), NoopFormattingEvents.INSTANCE);
  }

  @Override
  public List<String> getDefaultExcludes() {
    final List<String> excludes = new ArrayList<>(DEFAULT_EXCLUDES);
    excludes.add("**/node_modules");
    excludes.add("**/node_modules/**");
    excludes.add("**/.profileconfig.json");
    return excludes;
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

    if (!resourceProperties.getProperties().equals(lastResourceProperties)) {
      currentEcCodeStyle = new ECCodeStyle(new ParsedECProperties(resourceProperties));
    }

    lastResourceProperties = resourceProperties.getProperties();

    ensureResourceIsRead(resource);

    formatter.formatFile(resource.getPath(), currentEcCodeStyle, (path, psiElement) -> {
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
