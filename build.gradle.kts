plugins {
    groovy
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version("1.3.72")
    id("com.github.ben-manes.versions") version ("0.29.0")
}

val projectGroupId: String by project
val projectVersion: String by project

group = projectGroupId
version = projectVersion

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://dl.bintray.com/openapi-processor/primary")
    }
}

project.ext {
    set("processorApiVersion", "1.1.0")

    set("bintrayUser", project.findProperty("BINTRAY_USER") ?: System.getenv("BINTRAY_USER") ?: "n/a")
    set("bintrayKey", project.findProperty("BINTRAY_KEY") ?: System.getenv("BINTRAY_KEY") ?: "n/a")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.swagger.parser.v3:swagger-parser:2.0.21") {
        exclude(group = "io.swagger.parser.v3", module = "swagger-parser-v2-converter")
        exclude(group = "io.swagger.core.v3", module = "swagger-annotations")
    }
    compileOnly("io.openapiprocessor:openapi-processor-api:${project.ext.get("processorApiVersion")}")

    testImplementation("net.bytebuddy:byte-buddy:1.10.14")
    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5") {
        exclude(group = "org.codehaus.groovy", module = "groovy-json")
        exclude(group = "org.codehaus.groovy", module = "groovy-macro")
        exclude(group = "org.codehaus.groovy", module = "groovy-sql")
        exclude(group = "org.codehaus.groovy", module = "groovy-templates")
        exclude(group = "org.codehaus.groovy", module = "groovy-xml")
    }
    testImplementation("io.github.java-diff-utils:java-diff-utils:4.7")
    testImplementation("io.openapiprocessor:openapi-processor-api:${project.ext.get("processorApiVersion")}")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<GroovyCompile> {
    dependsOn("compileKotlin")
    classpath += files(tasks.compileKotlin.get().destinationDir)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn("javadoc")
    archiveClassifier.set("javadoc")
    from(tasks.javadoc.get().destinationDir)
}

artifacts {
    archives(sourcesJar)
    archives(javadocJar)
}




val projectTitle: String by project
val projectDesc: String by project
val projectUrl: String by project
val projectGithubRepo: String by project
val bintrayUser: String by project.ext
val bintrayKey: String by project.ext

publishing {
    publications {
        create<MavenPublication>("OpenApiProcessor") {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            pom {
                name.set(projectTitle)
                description.set("$projectTitle - $projectDesc - ${project.name} module")
                url.set(projectUrl)

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("hauner")
                        name.set("Martin Hauner")
                    }
                }

                scm {
                   url.set("https://github.com/${projectGithubRepo}")
                }
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = "https://api.bintray.com/maven/openapi-processor/primary/${project.name}/;publish=1;override=0"
            val snapshotsRepoUrl = "https://oss.jfrog.org/oss-snapshot-local/"
            url = uri(if (hasSnapshotVersion()) snapshotsRepoUrl else releasesRepoUrl)

            credentials {
                username = project.ext.get("bintrayUser").toString()
                password = project.ext.get("bintrayKey").toString()
            }
        }
    }
}

registerPublishTask("snapshot") { hasSnapshotVersion() }
registerPublishTask("release") { !hasSnapshotVersion() }

fun registerPublishTask(type: String, condition: () -> Boolean) {
    tasks.register("publish${type.capitalize()}") {
        group = "publishing"
        description = "Publish only if the current version is a ${type.capitalize()} version"

        if (condition()) {
            println("enabling $type publishing")
            dependsOn(tasks.withType<PublishToMavenRepository>())
        } else {
            doLast {
                println("skipping - no $type version")
            }
        }
    }
}

fun hasSnapshotVersion(): Boolean {
    return version.toString().endsWith("SNAPSHOT")
}
