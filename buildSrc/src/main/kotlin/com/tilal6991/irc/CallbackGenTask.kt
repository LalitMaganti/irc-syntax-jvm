package com.tilal6991.irc

import com.squareup.javapoet.*
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File
import java.net.URLClassLoader
import javax.annotation.Nonnull
import javax.lang.model.element.Modifier

open class CallbackGenTask : SourceTask() {
  private val SLASH = File.separator

  private val canonicalCallbackTypeVariable = TypeVariableName.get("T")
  private val callbackClassName = ClassName.get("com.tilal6991.irc", "MessageCallback")
  private val parameterizedCallbackName =
      ParameterizedTypeName.get(callbackClassName, canonicalCallbackTypeVariable)
  private val abstractClassName = ClassName.get("com.tilal6991.irc", "AbstractMessageCallback")

  @TaskAction
  @Suppress("unused", "UNUSED_PARAMETER")
  fun generate(inputs: IncrementalTaskInputs) {
    val container = project.property("sourceSets") as SourceSetContainer
    val classesDir = container.getByName("main").output.classesDir

    val projectDir = project.projectDir.absolutePath.removeSuffix("-core")
    val output = File("$projectDir${SLASH}src${SLASH}main${SLASH}java")

    val loader = URLClassLoader.newInstance(
        arrayOf(classesDir.toURI().toURL()), Nonnull::class.java.classLoader)

    val name = NameCallback(loader.loadClass(callbackClass("NamesParser")))

    val code = CodeCallback(
        loader.loadClass(callbackClass("CodeParser")), ClassName.get(name.klass.enclosingClass))

    val argument = ArgumentCallback(
        loader.loadClass(callbackClass("ArgumentParser")), ClassName.get(code.klass.enclosingClass))

    val tokenizer = TokenizerCallback(
        loader.loadClass(callbackClass("MessageTokenizer")),
        ClassName.get(argument.klass.enclosingClass))
    val tokenizerName = ClassName.get(tokenizer.klass.enclosingClass)

    val flattenedCallback = generateFlattenedCallback(argument, code, name)
    JavaFile.builder("com.tilal6991.irc", flattenedCallback).build().writeTo(output)

    val abstractCallback = generateAbstractCallback(flattenedCallback)
    JavaFile.builder("com.tilal6991.irc", abstractCallback).build().writeTo(output)

    val generateNestedParser = generateParser(tokenizerName, tokenizer, argument, code, name)
    JavaFile.builder("com.tilal6991.irc", generateNestedParser).build().writeTo(output)
  }

  private fun generateAbstractCallback(flattenedCallback: TypeSpec): TypeSpec? {
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

  private fun generateFlattenedCallback(vararg callbacks: Callback): TypeSpec {
    return TypeSpec.interfaceBuilder(callbackClassName)
        .addModifiers(Modifier.PUBLIC)
        .addMethods(
            callbacks.flatMap { it.generateFlattenedCallbackMethods() }
                .sortedBy { it.name })
        .addTypeVariable(canonicalCallbackTypeVariable)
        .build()
  }

  private fun generateParser(tokenizer: ClassName, vararg callbacks: Callback): TypeSpec {
    val innerClassName = ClassName.get("com.tilal6991.irc", "MessageParser", "Inner")
    return outerClass(innerClassName, tokenizer)
        .addType(innerClass(innerClassName, *callbacks).build())
        .build()
  }

  private fun innerClass(inner: ClassName, vararg callbacks: Callback): TypeSpec.Builder {
    return TypeSpec.classBuilder(inner)
        .addSuperinterfaces(
            callbacks.map {
              ParameterizedTypeName.get(ClassName.get(it.klass), canonicalCallbackTypeVariable)
            })
        .addModifiers(Modifier.PRIVATE)
        .addField(STRING_LIST_CLASS, "tags", Modifier.PRIVATE)
        .addField(STRING_CLASS, "prefix", Modifier.PRIVATE)
        .addField(STRING_CLASS, "target", Modifier.PRIVATE)
        .addMethods(callbacks.flatMap { it.generateMessageParserMethods() }.sortedBy { it.name })
  }

  private fun outerClass(innerClassName: ClassName, tokenizer: ClassName): TypeSpec.Builder {
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

    return TypeSpec.classBuilder(ClassName.get("com.tilal6991.irc", "MessageParser"))
        .addModifiers(Modifier.PUBLIC)
        .addField(parameterizedCallbackName, "callback", Modifier.PRIVATE, Modifier.FINAL)
        .addField(innerClassName, "inner", Modifier.PRIVATE, Modifier.FINAL)
        .addMethod(callbackConstructor)
        .addMethod(parseMethod)
        .addTypeVariable(canonicalCallbackTypeVariable)
  }

  private fun callbackClass(outer: String): String {
    return "com.tilal6991.irc.$outer\$Callback"
  }
}