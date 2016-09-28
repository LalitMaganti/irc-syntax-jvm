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
  private val callbackClassName = ClassName.get("com.tilal6991.irc", "MessageCallback")

  @TaskAction
  @Suppress("unused", "UNUSED_PARAMETER")
  fun generate(inputs: IncrementalTaskInputs) {
    val container = project.property("sourceSets") as SourceSetContainer
    val main = container.getByName("main")
    val classesDir = main.output.classesDir

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

    val projectDir = project.projectDir.absolutePath.removeSuffix("-core")
    val output = File("$projectDir${SLASH}src${SLASH}main${SLASH}java")
    val flattenedCallback = generateFlattenedCallback(argument, code, name)
    flattenedCallback.writeTo(output)

    val generateNestedParser = generateNestedParser(tokenizer, argument, code, name)
    generateNestedParser.writeTo(output)
  }

  private fun generateFlattenedCallback(vararg callbacks: Callback): JavaFile {
    return TypeSpec.interfaceBuilder(callbackClassName)
        .addModifiers(Modifier.PUBLIC)
        .addMethods(
            callbacks.flatMap { it.generateFlattenedCallbackMethods() }
                .sortedBy { it.name })
        .build()
        .run { JavaFile.builder("com.tilal6991.irc", this) }
        .build()
  }

  private fun generateNestedParser(
      tokenizer: Callback, argument: Callback, code: Callback, name: Callback): JavaFile {
    val innerClassName = ClassName.get("com.tilal6991.irc", "MessageParser", "Inner")
    val inner = innerClass(innerClassName, tokenizer, argument, code, name)
    return outerClass(inner, innerClassName, tokenizer)
        .run { JavaFile.builder("com.tilal6991.irc", this) }
        .build()
  }

  private fun innerClass(inner: ClassName, vararg callbacks: Callback): TypeSpec {
    return TypeSpec.classBuilder(inner)
        .addSuperinterfaces(callbacks.map { ClassName.get(it.klass) })
        .addModifiers(Modifier.PRIVATE)
        .addField(STRING_LIST_CLASS, "tags", Modifier.PRIVATE)
        .addField(STRING_CLASS, "prefix", Modifier.PRIVATE)
        .addField(STRING_CLASS, "target", Modifier.PRIVATE)
        .addMethods(callbacks.flatMap { it.generateMessageParserMethods() }.sortedBy { it.name })
        .build()
  }

  private fun outerClass(
      inner: TypeSpec, innerClassName: ClassName, tokenizer: Callback): TypeSpec? {
    val callbackConstructor = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec.builder(callbackClassName, "callback")
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
        .addStatement("\$T.tokenize(line, inner)", tokenizer.klass.enclosingClass)
        .build()

    return TypeSpec.classBuilder(ClassName.get("com.tilal6991.irc", "MessageParser"))
        .addModifiers(Modifier.PUBLIC)
        .addField(callbackClassName, "callback", Modifier.PRIVATE, Modifier.FINAL)
        .addField(innerClassName, "inner", Modifier.PRIVATE, Modifier.FINAL)
        .addMethod(callbackConstructor)
        .addMethod(parseMethod)
        .addType(inner)
        .build()
  }

  private fun callbackClass(outer: String): String {
    return "com.tilal6991.irc.$outer\$Callback"
  }
}