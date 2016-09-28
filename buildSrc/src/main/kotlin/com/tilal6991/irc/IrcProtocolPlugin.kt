package com.tilal6991.irc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer

class IrcProtocolPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.afterEvaluate {
      val sourceSets = project.properties["sourceSets"] as SourceSetContainer
      val outputDir = sourceSets.flatMap { it.allJava.srcDirs }.first().absolutePath

      // Create a "generateKotlinFor" task for generating kotlin bindings
      val task = project.tasks.create("generateCallback", CallbackGenTask::class.java)
      task.dependsOn.add("classes")

      val genTask = task.apply {
        source(outputDir)
      }

      genTask.outputs.upToDateWhen { false }
      project.tasks.add(genTask)
    }
  }
}