package com.tilal6991.irc.syntax

import com.squareup.javapoet.*
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.lang.model.element.Modifier

val outputPackage = "com.tilal6991.irc.syntax"

abstract class Generator(val klass: Class<*>) {
  protected val callbackMethods: List<Method> = klass.declaredMethods.toList()

  abstract fun callbackMethods(): Iterable<MethodSpec>
  abstract fun parserMethods(): Iterable<MethodSpec>
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
}

class ArgumentGenerator(callbackClass: Class<*>,
                        private val codeParser: ClassName,
                        private val capParser: ClassName) : Generator(callbackClass) {

  override fun callbackMethods(): Iterable<MethodSpec> {
    return callbackMethods.asSequence()
        .filter { it.name != "onReply" && it.name != "onCap" }
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
      } else if (it.name == "onCap") {
        overriding(it)
            .addStatement("return \$T.parse(arguments, this)", capParser)
            .build()
      } else {
        overriding(it)
            .addStatement("return callback.${it.name}(tags, prefix, ${joinParams(it.parameters)})")
            .build()
      }
    }
  }
}

class ClientCapGenerator(callbackClass: Class<*>) : Generator(callbackClass) {
  override fun callbackMethods(): Iterable<MethodSpec> {
    return callbackMethods
        .map {
          createCanonicalMethodBuilder(it)
              .addTokenizerParameters()
              .addParametersFromDeclaration(it)
              .addJavadoc(
                  "Callback method for CAP ${it.name.removePrefix("onCap").toUpperCase()}.\n")
              .build()
        }
  }

  override fun parserMethods(): Iterable<MethodSpec> {
    return callbackMethods.map {
      overriding(it)
          .addStatement("return callback.${it.name}(tags, prefix, ${joinParams(it.parameters)})")
          .build()
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

fun MethodSpec.Builder.addCodeParameters(): MethodSpec.Builder {
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