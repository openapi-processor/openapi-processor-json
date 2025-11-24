import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    groovy
    kotlin
    jacoco
    alias(libs.plugins.updates)
    id("openapiprocessor.test")
    id("openapiprocessor.testInt")
    id("openapiprocessor.publish")
    id("jacoco-report-aggregation")
}

val projectGroupId: String by project
val projectVersion: String by project

group = "io.openapiprocessor"
version = libs.versions.processor.get()
println("version: $version")

java {
    withJavadocJar ()
    withSourcesJar ()

    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(libs.versions.build.jdk.get().toInt())

    compilerOptions {
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
        jvmTarget = JvmTarget.fromTarget(libs.versions.target.jdk.get())
    }
}

tasks.compileTestGroovy {
    classpath += sourceSets.main.get().compileClasspath
    classpath += files(tasks.compileKotlin.get().destinationDirectory)
    classpath += files(tasks.compileTestKotlin.get().destinationDirectory)
}

dependencies {
    implementation (libs.openapi.processor.api)
    implementation (platform(libs.openapi.parser.bom))
    implementation (libs.openapi.parser.parser)
    implementation (libs.io.jackson)

    testImplementation(libs.diff)
    testImplementation (platform(libs.groovy.bom))
    testImplementation ("org.apache.groovy:groovy")
    testImplementation ("org.apache.groovy:groovy-nio")
    testImplementation (libs.spock)
    testImplementation (platform(libs.kotest.bom))
    testImplementation (libs.kotest.runner)
    testImplementation (libs.kotest.table)
    testImplementation (libs.mockk)
    testImplementation (libs.logback)

    testIntImplementation(libs.diff)
    testIntImplementation (platform(libs.groovy.bom))
    testIntImplementation ("org.apache.groovy:groovy")
    testIntImplementation ("org.apache.groovy:groovy-nio")
    testIntImplementation (libs.spock)
    testIntImplementation (platform(libs.kotest.bom))
    testIntImplementation (libs.kotest.runner)
    testIntImplementation (libs.kotest.table)
    testIntImplementation (libs.mockk)
    testIntImplementation (libs.logback)
}

tasks.withType<Test>().configureEach {
//    jvmArgs(listOf(
//        "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
//        "--add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
//        "--add-exports", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
//        "--add-exports", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
//        "--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
//        "--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
//    ))

    finalizedBy(tasks.named("jacocoTestReport"))
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.jacocoTestReport {
    executionData.from(tasks.named<Test>("test").map<File> {
        it.extensions.getByType(JacocoTaskExtension::class.java).destinationFile as File
    })
    executionData.from(tasks.named<Test>("testInt").map<File> {
        it.extensions.getByType(JacocoTaskExtension::class.java).destinationFile as File
    })

    reports {
        xml.required = true
        html.required = true
    }
}
