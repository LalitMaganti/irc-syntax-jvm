package com.tilal6991.irc.syntax

import com.squareup.javapoet.*
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.lang.model.element.Modifier

val outputPackage = "com.tilal6991.irc.syntax"
val messageClassName = ClassName.get(outputPackage, "Message")!!
val replyMessageClassName = ClassName.get(outputPackage, "Message", "Reply")!!

abstract class Generator(val klass: Class<*>) {
  protected val callbackMethods: List<Method> = klass.declaredMethods.toList()

  abstract fun callbackMethods(): Iterable<MethodSpec>
  abstract fun parserMethods(): Iterable<MethodSpec>
  abstract fun messages(): Iterable<TypeSpec>
}

class TokenizerGenerator(callbackClass: Class<*>,
                         private val argumentParser: ClassName) : Generator(callbackClass) {
  override fun callbackMethods(): Iterable<MethodSpec> {
    return emptyList()
  }

  override fun parserMethods(): Iterable<MethodSpec> {
    return callbackMethods.map {
      overriding(it)
          .addTokenizerAssignment()
          .addStatement("T temp = \$T.parse(command, arguments, this)", argumentParser)
          .addStatement("this.tags = null")
          .addStatement("this.prefix = null")
          .addStatement("return temp")
          .build()
    }
  }

  override fun messages(): Iterable<TypeSpec> {
    return emptyList()
  }
}

class ArgumentGenerator(callbackClass: Class<*>,
                        private val codeParser: ClassName) : Generator(callbackClass) {

  override fun callbackMethods(): Iterable<MethodSpec> {
    return callbackMethods.asSequence()
        .filter { it.name != "onReply" }
        .map {
          val name = it.name.removePrefix("on").toUpperCase()
          createCanonicalMethodBuilder(it)
              .addTokenizerParameters()
              .addParametersFromDeclaration(it)
              .addJavadoc("Callback method for $name messages.\n")
              .build()
        }
        .toList()
  }

  override fun parserMethods(): Iterable<MethodSpec> {
    return callbackMethods.map {
      if (it.name == "onReply") {
        overriding(it)
            .addStatement("this.target = target")
            .addStatement("T temp = \$T.parse(code, arguments, this)", codeParser)
            .addStatement("this.target = null")
            .addStatement("return temp")
            .build()
      } else {
        overriding(it)
            .addStatement("return callback.${it.name}(tags, prefix, ${joinParams(it.parameters)})")
            .build()
      }
    }
  }

  override fun messages(): Iterable<TypeSpec> {
    return callbackMethods.map {
      val constructor = MethodSpec.constructorBuilder()
          .addTokenizerParameters()
          .addModifiers(Modifier.PUBLIC)
          .addStatement("super(tags, prefix)")

      val builder = TypeSpec.classBuilder(it.name.removePrefix("on"))
          .superclass(messageClassName)
          .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
      if (it.name == "onReply") {
        builder.addCodeFields()
        constructor.addCodeParameters()
            .addStatement("this.target = target")
      } else {
        builder.addFieldsFromDeclaration(it)
            .addModifiers(Modifier.FINAL)
        constructor
            .addParametersFromDeclaration(it)
            .apply {
              it.parameters
                  .filter { it.name != "tags" && it.name != "prefix" }
                  .asSequence()
                  .map { "this.${it.name} = ${it.name}" }
                  .forEach { addStatement(it) }
            }
      }
      builder.addMethod(constructor.build()).build()
    }
  }
}

class CodeGenerator(callbackClass: Class<*>, val nameParser: ClassName) : Generator(callbackClass) {
  override fun callbackMethods(): Iterable<MethodSpec> {
    return callbackMethods
        .filter { it.name != "onNamReply" }
        .map {
          createCanonicalMethodBuilder(it)
              .addTokenizerParameters()
              .addCodeParameters()
              .addParametersFromDeclaration(it)
              .addJavadoc(
                  "Callback method for RPL_${it.name.removePrefix("on").toUpperCase()} replies.\n")
              .build()
        }
  }

  override fun parserMethods(): Iterable<MethodSpec> {
    return callbackMethods.map {
      if (it.name == "onNamReply") {
        overriding(it)
            .addStatement("return \$T.parse(arguments, this)", nameParser)
            .build()
      } else {
        generateCodeParserMethodFromCallbackMethod(it)
      }
    }
  }

  override fun messages(): Iterable<TypeSpec> {
    return callbackMethods
        .filter { it.name != "onNamReply" }
        .map { generateCodeMessageFromCallbackMethod(it) }
  }
}

class NameGenerator(callbackClass: Class<*>) : Generator(callbackClass) {
  override fun callbackMethods(): Iterable<MethodSpec> {
    return callbackMethods.map {
      createCanonicalMethodBuilder(it)
          .addTokenizerParameters()
          .addCodeParameters()
          .addParametersFromDeclaration(it)
          .addJavadoc("Callback method for RPL_NAMREPLY replies.\n")
          .build()
    }
  }

  override fun parserMethods(): Iterable<MethodSpec> {
    return callbackMethods.map {
      overriding(it)
          .addStatement(
              "return callback.${it.name}(tags, prefix, target, ${joinParams(it.parameters)})")
          .build()
    }
  }

  override fun messages(): Iterable<TypeSpec> {
    return callbackMethods.map { generateCodeMessageFromCallbackMethod(it) }
  }
}

private fun joinParams(params: Array<out Parameter>): String {
  return params.asSequence()
      .map { it.name }
      .joinToString(", ")
}

private fun createCanonicalMethodBuilder(it: Method): MethodSpec.Builder {
  return MethodSpec.methodBuilder(it.name)
      .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
      .returns(TypeVariableName.get("T"))
}

private fun generateCodeParserMethodFromCallbackMethod(it: Method): MethodSpec {
  return overriding(it)
      .addStatement(
          "return callback.${it.name}(tags, prefix, target, ${joinParams(it.parameters)})")
      .build()
}

private fun generateCodeMessageFromCallbackMethod(method: Method): TypeSpec {
  val constructor = MethodSpec.constructorBuilder()
      .addTokenizerParameters()
      .addCodeParameters()
      .addModifiers(Modifier.PUBLIC)
      .addStatement("super(tags, prefix, target)")
      .addParametersFromDeclaration(method)
      .apply {
        method.parameters
            .filter { it.name != "tags" && it.name != "prefix" && it.name != "target" }
            .asSequence()
            .map { "this.${it.name} = ${it.name}" }
            .forEach { addStatement(it) }
      }
      .build()

  val builder = TypeSpec.classBuilder(method.name.removePrefix("on"))
      .superclass(replyMessageClassName)
      .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
      .addFieldsFromDeclaration(method)
  return builder.addMethod(constructor).build()
}

fun TypeSpec.Builder.addTokenizerFields(): TypeSpec.Builder {
  return addField(
      FieldSpec.builder(STRING_LIST_CLASS, "tags")
          .addAnnotation(Nullable::class.java)
          .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
          .build())
      .addField(
          FieldSpec.builder(STRING_CLASS, "prefix")
              .addAnnotation(Nullable::class.java)
              .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
              .build())
}

fun MethodSpec.Builder.addTokenizerParameters(): MethodSpec.Builder {
  // Add the tokenizer's tags and prefix here.
  return addParameter(
      ParameterSpec.builder(STRING_LIST_CLASS, "tags")
          .addAnnotation(Nullable::class.java)
          .build())
      .addParameter(
          ParameterSpec.builder(STRING_CLASS, "prefix")
              .addAnnotation(Nullable::class.java)
              .build())
}

fun MethodSpec.Builder.addTokenizerAssignment(): MethodSpec.Builder {
  return addStatement("this.tags = tags")
      .addStatement("this.prefix = prefix")
}

fun TypeSpec.Builder.addCodeFields(): TypeSpec.Builder {
  // Add the reply's target here.
  return addField(FieldSpec.builder(STRING_CLASS, "target")
      .addAnnotation(Nonnull::class.java)
      .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
      .build())
}

fun MethodSpec.Builder.addCodeParameters(): MethodSpec.Builder {
  // Add the reply's target here.
  return addParameter(ParameterSpec.builder(STRING_CLASS, "target")
      .addAnnotation(Nonnull::class.java)
      .build())
}

private fun TypeSpec.Builder.addFieldsFromDeclaration(method: Method): TypeSpec.Builder {
  return this.apply {
    method.parameters.forEach {
      addField(FieldSpec.builder(TypeName.get(it.parameterizedType), it.name)
          .addAnnotations(it.annotations.map { AnnotationSpec.get(it) })
          .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
          .build())
    }
  }
}

private fun MethodSpec.Builder.addParametersFromDeclaration(method: Method): MethodSpec.Builder {
  return this.apply {
    method.parameters.forEach {
      addParameter(ParameterSpec.builder(TypeName.get(it.parameterizedType), it.name)
          .addAnnotations(it.annotations.map { AnnotationSpec.get(it) })
          .build())
    }
  }
}