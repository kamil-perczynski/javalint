package com.javalint.codegen;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static com.javalint.codegen.JavaLintCodegen.toClazz;
import static java.lang.reflect.Modifier.isStatic;

public class CodegenField {

  public static boolean isCodeStyleField(Field declaredField) {
    return !isStatic(declaredField.getModifiers()) && isScreamingCase(declaredField.getName());
  }

  private final Field field;

  public CodegenField(Field field) {
    this.field = field;
  }

  public String getName() {
    return field.getName();
  }

  public boolean isInteger() {
    return field.getType().isAssignableFrom(int.class);
  }

  public boolean isString() {
    return field.getType().isAssignableFrom(String.class);
  }

  public boolean isBoolean() {
    return field.getType().isAssignableFrom(boolean.class);
  }

  public boolean isWrapConstant() {
    return hasAnnotation("com.intellij.psi.codeStyle.CommonCodeStyleSettings$WrapConstant");
  }

  public boolean isForceBraceConstant() {
    return hasAnnotation("com.intellij.psi.codeStyle.CommonCodeStyleSettings$ForceBraceConstant");
  }

  public boolean isBraceStyleConstant() {
    return hasAnnotation("com.intellij.psi.codeStyle.CommonCodeStyleSettings$BraceStyleConstant");
  }

  public String getExternalName() {
    final Class<Annotation> propertyClass = toClazz("com.intellij.configurationStore.Property");

    final Annotation propertyAnnotation = field.getAnnotation(propertyClass);

    return Optional.ofNullable(propertyAnnotation)
      .map(annotation -> toExternalName(annotation, propertyAnnotation))
      .orElseGet(() -> field.getName().toLowerCase());
  }

  private boolean hasAnnotation(String annotationClassName) {
    for (Annotation annotation : field.getAnnotations()) {
      if (annotation.annotationType().getName().equals(annotationClassName)) {
        return true;
      }
    }
    return false;
  }

  private static String toExternalName(Annotation a, Annotation propertyAnnotation) {
    try {
      return (String) a.annotationType().getMethod("externalName").invoke(propertyAnnotation);
    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean isScreamingCase(String name) {
    return name.chars().allMatch(it -> it == '_' || Character.isUpperCase(it));
  }

}
