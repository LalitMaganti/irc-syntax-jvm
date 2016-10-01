package com.tilal6991.irc.syntax

import com.squareup.javapoet.*
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File
import java.net.URLClassLoader
import java.util.*
import javax.annotation.Nonnull
import javax.lang.model.element.Modifier

open class CallbackGenTask : SourceTask() {
  private val SLASH = File.separator

  private val callbackClassName = ClassName.get(outputPackage, "ClientMessageCallback")
  private val abstractClassName = ClassName.get(outputPackage, "AbstractClientMessageCallback")
  private val delegatingClassName = ClassName.get(outputPackage, "DelegatingClientMessageCallback")

  private val canonicalCallbackTypeVariable = TypeVariableName.get("T")
  private val parameterizedCallbackName =
      ParameterizedTypeName.get(callbackClassName, canonicalCallbackTypeVariable)

  @TaskAction
  @Suppress("unused", "UNUSED_PARAMETER")
  fun generate(inputs: IncrementalTaskInputs) {
    val container = project.property("sourceSets") as SourceSetContainer
    val classesDir = container.getByName("main").output.classesDir

    val projectDir = project.projectDir.absolutePath.removeSuffix("-core")
    val output = File("$projectDir${SLASH}src${SLASH}main${SLASH}java")

    val loader = URLClassLoader.newInstance(
        arrayOf(classesDir.toURI().toURL()), Nonnull::class.java.classLoader)

    val name = NameGenerator(loader.loadClass(callbackClass("NamesParser")))

    val code = CodeGenerator(
        loader.loadClass(callbackClass("CodeParser")), ClassName.get(name.klass.enclosingClass))

    val clientCap = ClientCapGenerator(loader.loadClass(callbackClass("ClientCapParser")))

    val argument = ArgumentGenerator(
        loader.loadClass(callbackClass("ArgumentParser")),
        ClassName.get(code.klass.enclosingClass),
        ClassName.get(clientCap.klass.enclosingClass))

    val tokenizer = TokenizerGenerator(
        loader.loadClass(callbackClass("MessageTokenizer")),
        ClassName.get(argument.klass.enclosingClass))
    val tokenizerName = ClassName.get(tokenizer.klass.enclosingClass)

    val flattenedCallback = generateFlattenedCallback(argument, clientCap, code, name)
    JavaFile.builder(outputPackage, flattenedCallback).build().writeTo(output)

    val abstractCallback = generateAbstractCallback(flattenedCallback)
    JavaFile.builder(outputPackage, abstractCallback).build().writeTo(output)

    val delegatingCallback = generateDelegatingCallback(flattenedCallback)
    JavaFile.builder(outputPackage, delegatingCallback).build().writeTo(output)

    val parser = generateParser(tokenizerName, tokenizer, argument, clientCap, code, name)
    JavaFile.builder(outputPackage, parser).build().writeTo(output)
  }

  private fun generateAbstractCallback(flattenedCallback: TypeSpec): TypeSpec {
    return TypeSpec.classBuilder(abstractClassName)
        .addModifiers(Modifier.PUBLIC)
        .addTypeVariable(canonicalCallbackTypeVariable)
        .addSuperinterface(parameterizedCallbackName)
        .addMethods(
            flattenedCallback.methodSpecs.map {
              overriding(it).addStatement("return null").build()
            })
        .build()
  }

  private fun generateDelegatingCallback(flattenedCallback: TypeSpec): TypeSpec {
    val callbackObject = ParameterizedTypeName.get(
        callbackClassName, WildcardTypeName.subtypeOf(TypeName.OBJECT))
    val callbacks = ParameterizedTypeName.get(LIST_CLASS, callbackObject)
    return TypeSpec.classBuilder(delegatingClassName)
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ParameterizedTypeName.get(callbackClassName, ClassName.VOID.box()))
        .addField(callbacks, "callbacks", Modifier.PRIVATE, Modifier.FINAL)
        .addMethod(MethodSpec.constructorBuilder()
            .addStatement("callbacks = new \$T<>()", TypeName.get(ArrayList::class.java))
            .build())
        .addMethods(flattenedCallback.methodSpecs.map {
          overriding(it)
              .returns(TypeName.VOID.box())
              .beginControlFlow("for (\$T callback : callbacks)", callbackObject)
              .addStatement(
                  "callback.${it.name}(${it.parameters.map { it.name }.joinToString(", ")})")
              .endControlFlow()
              .addStatement("return null")
              .build()
        })
        .addMethod(MethodSpec.methodBuilder("addCallback")
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.VOID)
            .addTypeVariable(canonicalCallbackTypeVariable)
            .addStatement("callbacks.add(callback)")
            .addParameter(parameterizedCallbackName, "callback")
            .build())
        .addMethod(MethodSpec.methodBuilder("removeCallback")
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.VOID)
            .addTypeVariable(canonicalCallbackTypeVariable)
            .addStatement("callbacks.remove(callback)")
            .addParameter(parameterizedCallbackName, "callback")
            .build())
        .build()
  }

  private fun generateFlattenedCallback(vararg generators: Generator): TypeSpec {
    return TypeSpec.interfaceBuilder(callbackClassName)
        .addModifiers(Modifier.PUBLIC)
        .addMethods(
            generators.flatMap { it.callbackMethods() }
                .sortedBy { it.name })
        .addTypeVariable(canonicalCallbackTypeVariable)
        .build()
  }

  private fun generateParser(tokenizer: ClassName, vararg generators: Generator): TypeSpec {
    val innerClassName = ClassName.get(outputPackage, "MessageParser", "Inner")
    return outerParserClass(innerClassName, tokenizer)
        .addType(innerParserClass(innerClassName, *generators).build())
        .build()
  }

  private fun innerParserClass(inner: ClassName, vararg generators: Generator): TypeSpec.Builder {
    return TypeSpec.classBuilder(inner)
        .addSuperinterfaces(
            generators.map {
              ParameterizedTypeName.get(ClassName.get(it.klass), canonicalCallbackTypeVariable)
            })
        .addModifiers(Modifier.PRIVATE)
        .addField(STRING_LIST_CLASS, "tags", Modifier.PRIVATE)
        .addField(STRING_CLASS, "prefix", Modifier.PRIVATE)
        .addField(STRING_CLASS, "target", Modifier.PRIVATE)
        .addMethods(generators.flatMap { it.parserMethods() }.sortedBy { it.name })
  }

  private fun outerParserClass(innerClassName: ClassName, tokenizer: ClassName): TypeSpec.Builder {
    val callbackConstructor = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec.builder(parameterizedCallbackName, "callback")
            .addAnnotation(Nonnull::class.java)
            .build())
        .addStatement("this.callback = callback")
        .addStatement("this.inner = new \$T()", innerClassName)
        .build()

    val parseMethod = MethodSpec.methodBuilder("parse")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec.builder(STRING_CLASS, "line")
            .addAnnotation(Nonnull::class.java)
            .build())
        .addStatement("return \$T.tokenize(line, inner)", tokenizer)
        .returns(canonicalCallbackTypeVariable)
        .build()

    return TypeSpec.classBuilder(ClassName.get(outputPackage, "MessageParser"))
        .addModifiers(Modifier.PUBLIC)
        .addField(parameterizedCallbackName, "callback", Modifier.PRIVATE, Modifier.FINAL)
        .addField(innerClassName, "inner", Modifier.PRIVATE, Modifier.FINAL)
        .addMethod(callbackConstructor)
        .addMethod(parseMethod)
        .addTypeVariable(canonicalCallbackTypeVariable)
  }

  private fun callbackClass(outer: String): String {
    return "$outputPackage.$outer\$Callback"
  }
}