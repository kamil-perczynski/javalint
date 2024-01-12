package io.github.kamilperczynski.javalint.codegen;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class JavaLintCodegen {

  private static final Logger LOG = LoggerFactory.getLogger(JavaLintCodegen.class);
  private static final Handlebars HANDLEBARS = new Handlebars(new ClassPathTemplateLoader());

  public static void main(String[] args) throws IOException {
    final String baseDir = args[0];
    final String outputDir = args[1];
    final String outputPackage = args[2];
    final String templatePath = args[3];
    final String classNames = args[4];

    final Path baseDirPath = Paths.get(baseDir);
    final List<? extends Class<?>> classes = parseClassesArg(classNames);

    final Template template = HANDLEBARS.compile(
      readTemplateSource(baseDirPath, templatePath)
    );

    final Path finalOutputDirectory = toGeneratedPackageDirectory(outputDir, outputPackage);
    Files.createDirectories(finalOutputDirectory);

    for (final Class<?> clazz : classes) {
      final JavaLintCodegenContext context = new JavaLintCodegenContext(outputPackage, clazz);

      final Path outputFile = finalOutputDirectory.resolve(context.getOutputClassName() + ".kt");
      LOG.info("Writing content into: {}", baseDirPath.relativize(outputFile));

      try (BufferedWriter out = Files.newBufferedWriter(outputFile)) {
        template.apply(context, out);
      }
    }
  }

  private static List<Class<Object>> parseClassesArg(String classNames) {
    return Arrays.stream(classNames.split(","))
      .map(JavaLintCodegen::toClazz)
      .toList();
  }

  private static TemplateSource readTemplateSource(Path baseDirPath,
                                                   String templateLocation) throws IOException {
    final Path templatePath = baseDirPath.resolve(templateLocation);

    return new StringTemplateSource("javalint-codegen.hbs", Files.readString(templatePath));
  }

  private static Path toGeneratedPackageDirectory(String outputDir, String outputPackage) {
    return Paths.get(outputDir).resolve(outputPackage.replace('.', '/'));
  }

  static <T> Class<T> toClazz(String className) {
    try {
      //noinspection unchecked
      return (Class<T>) Class.forName(className.trim());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

}
