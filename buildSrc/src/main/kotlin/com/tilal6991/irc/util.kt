package com.tilal6991.irc

import com.squareup.javapoet.*
import java.lang.reflect.Method
import java.lang.reflect.Modifier.*
import java.util.*
import javax.lang.model.element.Modifier

val LIST_CLASS = ClassName.get(List::class.java)!!
val STRING_CLASS = ClassName.get(String::class.java)!!
val STRING_LIST_CLASS = ParameterizedTypeName.get(LIST_CLASS, STRING_CLASS)!!

private val OVERRIDE = ClassName.get(Override::class.java)
fun overridingReflect(method: Method): MethodSpec.Builder {
  val modifiers = method.modifiers
  if (isPrivate(modifiers)
      || isFinal(modifiers)
      || isStatic(modifiers)) {
    throw IllegalArgumentException("cannot override method with modifiers: " + modifiers)
  }

  val methodBuilder = MethodSpec.methodBuilder(method.name)
  methodBuilder.addAnnotation(OVERRIDE)
  for (annotation in method.annotations) {
    val annotationSpec = AnnotationSpec.get(annotation)
    if (annotationSpec.type == OVERRIDE) continue
    methodBuilder.addAnnotation(annotationSpec)
  }

  val modifierSet = modifiersToModifierSet(method.modifiers)
  modifierSet.remove(Modifier.ABSTRACT)
  methodBuilder.addModifiers(modifierSet)

  for (typeParameterElement in method.typeParameters) {
    methodBuilder.addTypeVariable(TypeVariableName.get(typeParameterElement))
  }

  methodBuilder.returns(TypeName.get(method.genericReturnType))

  for (parameter in method.parameters) {
    val type = TypeName.get(parameter.parameterizedType)
    val parameterBuilder = ParameterSpec.builder(type, parameter.name)
        .addModifiers(*modifiersToModifierSet(parameter.modifiers).toTypedArray())
    for (mirror in parameter.annotations) {
      parameterBuilder.addAnnotation(AnnotationSpec.get(mirror))
    }
    methodBuilder.addParameter(parameterBuilder.build())
  }
  methodBuilder.varargs(method.isVarArgs)

  for (thrownType in method.exceptionTypes) {
    methodBuilder.addException(TypeName.get(thrownType))
  }

  return methodBuilder
}

fun overridingSpec(method: MethodSpec): MethodSpec.Builder {
  val modifiers = method.modifiers
  if (modifiers.contains(Modifier.PRIVATE)
      || modifiers.contains(Modifier.FINAL)
      || modifiers.contains(Modifier.STATIC)) {
    throw IllegalArgumentException("cannot override method with modifiers: " + modifiers)
  }

  val methodBuilder = MethodSpec.methodBuilder(method.name)
  methodBuilder.addAnnotation(OVERRIDE)
  for (annotationSpec in method.annotations) {
    if (annotationSpec.type == OVERRIDE) continue
    methodBuilder.addAnnotation(annotationSpec)
  }

  val modifierSet = LinkedHashSet(method.modifiers)
  modifierSet.remove(Modifier.ABSTRACT)
  methodBuilder.addModifiers(modifierSet)

  for (typeParameterElement in method.typeVariables) {
    methodBuilder.addTypeVariable(typeParameterElement)
  }

  methodBuilder.returns(method.returnType)

  for (parameter in method.parameters) {
    val parameterBuilder = ParameterSpec.builder(parameter.type, parameter.name)
        .addModifiers(*parameter.modifiers.toTypedArray())
    for (annotationSpec in parameter.annotations) {
      parameterBuilder.addAnnotation(annotationSpec)
    }
    methodBuilder.addParameter(parameterBuilder.build())
  }
  methodBuilder.varargs(method.varargs)

  for (thrownType in method.exceptions) {
    methodBuilder.addException(thrownType)
  }

  return methodBuilder
}

// TODO(tilal6991) This is missing DEFAULT modifier - we don't care right now but add it in
// later if it becomes a problem. Info can be found in Method's isDefault boolean.
private fun modifiersToModifierSet(modifiers: Int): LinkedHashSet<Modifier> {
  val modifierSet = LinkedHashSet<Modifier>()
  if (isPublic(modifiers)) {
    modifierSet.add(Modifier.PUBLIC)
  }
  if (isProtected(modifiers)) {
    modifierSet.add(Modifier.PROTECTED)
  }
  if (isPrivate(modifiers)) {
    modifierSet.add(Modifier.PRIVATE)
  }
  if (isAbstract(modifiers)) {
    modifierSet.add(Modifier.ABSTRACT)
  }
  if (isStatic(modifiers)) {
    modifierSet.add(Modifier.STATIC)
  }
  if (isFinal(modifiers)) {
    modifierSet.add(Modifier.FINAL)
  }
  if (isTransient(modifiers)) {
    modifierSet.add(Modifier.TRANSIENT)
  }
  if (isVolatile(modifiers)) {
    modifierSet.add(Modifier.VOLATILE)
  }
  if (isSynchronized(modifiers)) {
    modifierSet.add(Modifier.SYNCHRONIZED)
  }
  if (isNative(modifiers)) {
    modifierSet.add(Modifier.NATIVE)
  }
  if (isStrict(modifiers)) {
    modifierSet.add(Modifier.STRICTFP)
  }
  return modifierSet
}