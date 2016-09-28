package com.tilal6991.irc

import com.squareup.javapoet.*
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.lang.model.element.Modifier

abstract class Callback(val klass: Class<*>) {
  protected val methods: List<Method> = klass.declaredMethods.toList()

  abstract fun generateFlattenedCallbackMethods(): Iterable<MethodSpec>
  abstract fun generateMessageParserMethods(): Iterable<MethodSpec>
}

class TokenizerCallback(klass: Class<*>, private val argumentParser: ClassName) : Callback(klass) {
  override fun generateFlattenedCallbackMethods(): Iterable<MethodSpec> {
    return emptyList()
  }

  override fun generateMessageParserMethods(): Iterable<MethodSpec> {
    return methods.map {
      overriding(it)
          .addStatement("this.tags = tags")
          .addStatement("this.prefix = prefix")
          .addStatement("T temp = \$T.parse(command, arguments, this)", argumentParser)
          .addStatement("this.tags = null")
          .addStatement("this.prefix = null")
          .addStatement("return temp")
          .build()
    }
  }
}

class ArgumentCallback(klass: Class<*>, private val codeParser: ClassName) : Callback(klass) {

  override fun generateFlattenedCallbackMethods(): Iterable<MethodSpec> {
    return methods.asSequence()
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

  override fun generateMessageParserMethods(): Iterable<MethodSpec> {
    return methods.map {
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
}

class CodeCallback(klass: Class<*>, val nameParser: ClassName) : Callback(klass) {
  override fun generateFlattenedCallbackMethods(): Iterable<MethodSpec> {
    return methods
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

  override fun generateMessageParserMethods(): Iterable<MethodSpec> {
    return methods.map {
      if (it.name == "onNamReply") {
        overriding(it)
            .addStatement("return \$T.parse(arguments, this)", nameParser)
            .build()
      } else {
        overriding(it)
            .addStatement(
                "return callback.${it.name}(tags, prefix, target, ${joinParams(it.parameters)})")
            .build()
      }
    }
  }
}

class NameCallback(klass: Class<*>) : Callback(klass) {
  override fun generateFlattenedCallbackMethods(): Iterable<MethodSpec> {
    return methods.map {
      createCanonicalMethodBuilder(it)
          .addTokenizerParameters()
          .addCodeParameters()
          .addParametersFromDeclaration(it)
          .addJavadoc("Callback method for RPL_NAMREPLY replies.\n")
          .build()
    }
  }

  override fun generateMessageParserMethods(): Iterable<MethodSpec> {
    return methods.map {
      overriding(it)
          .addStatement(
              "return callback.${it.name}(tags, prefix, target, ${joinParams(it.parameters)})")
          .build()
    }
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

private fun MethodSpec.Builder.addTokenizerParameters(): MethodSpec.Builder {
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

private fun MethodSpec.Builder.addCodeParameters(): MethodSpec.Builder {
  // Add the reply's target here.
  return addParameter(ParameterSpec.builder(STRING_CLASS, "target")
      .addAnnotation(Nonnull::class.java)
      .build())
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