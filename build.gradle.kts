import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
  id("xyz.jpenilla.run-paper") version "2.3.1"
  id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
    kotlin("jvm")
  id("com.gradleup.shadow") version "9.0.0-beta10"
}

group = "io.papermc.paperweight"
version = "1.0.0-SNAPSHOT"
description = "Test plugin for paperweight-userdev"

java {
  toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.assemble {
  dependsOn(tasks.shadowJar)
}

dependencies {
  paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
  // paperweight.foliaDevBundle("1.21.4-R0.1-SNAPSHOT")
  // paperweight.devBundle("com.example.paperfork", "1.21.4-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
  implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.21.0")
  implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.21.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
  implementation("com.jeff-media:custom-block-data:2.2.4")
}

tasks {
  compileJava {
    options.release = 21
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }
  /*shadowJar {
    relocate 'com.jeff_media.customblockdata', 'your.package.customblockdata'
  }*/
  shadowJar {
    relocate("com.jeff_media.customblockdata", "org.gang.customblockdata")
  }
}

bukkitPluginYaml {
  main = "org.gang.TestPlugin"
  load = BukkitPluginYaml.PluginLoadOrder.STARTUP
  authors.add("Author")
  apiVersion = "1.21.4"
  libraries = listOf(
    "org.jetbrains.kotlin:kotlin-stdlib:2.1.10",
    "com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.21.0",
    "com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.21.0",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1"
  )
}
repositories {
    mavenCentral()
}
