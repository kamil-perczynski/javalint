package com.javalint.codegen;

import java.util.Arrays;
import java.util.List;

public class JavaLintCodegenContext {

  public static List<CodegenField> detectCodeStyleFields(Class<?> clazz) {
    return Arrays.stream(clazz.getDeclaredFields())
      .filter(CodegenField::isCodeStyleField)
      .map(CodegenField::new)
      .toList();
  }

  private final String outputPackage;
  private final Class<?> clazz;

  public JavaLintCodegenContext(String outputPackage,
                                Class<?> clazz) {
    this.outputPackage = outputPackage;
    this.clazz = clazz;
  }

  public String getOutputPackage() {
    return outputPackage;
  }

  public String getClassName() {
    return clazz.getName();
  }

  public String getSimpleName() {
    return clazz.getSimpleName();
  }

  public List<CodegenField> getFields() {
    return detectCodeStyleFields(clazz);
  }

  public String getOutputClassName() {
    return clazz.getSimpleName().replace("CodeStyleSettings", "ECCodeStyleAdapter");
  }

}
