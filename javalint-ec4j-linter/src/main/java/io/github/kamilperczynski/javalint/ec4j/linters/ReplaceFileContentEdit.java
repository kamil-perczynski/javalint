package io.github.kamilperczynski.javalint.ec4j.linters;

import org.ec4j.lint.api.Edit;
import org.ec4j.lint.api.Resource;

import java.nio.file.Path;

public record ReplaceFileContentEdit(Path file, String nextContent) implements Edit {

  @Override
  public void perform(Resource resource, int offset) {
    resource.replace(0, resource.length(), nextContent);
  }

  @Override
  public String getMessage() {
    return toFileExtension(file).toUpperCase() + " file";
  }

  private static String toFileExtension(Path path) {
    final String fileName = path.getFileName().toString();
    final int index = fileName.lastIndexOf('.');

    if (index == -1) {
      return "";
    }

    return fileName.substring(index + 1);
  }

}
