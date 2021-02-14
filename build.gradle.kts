plugins {
    groovy
    id("maven-publish")
    id("signing")
    id("org.jetbrains.kotlin.jvm") version("1.4.30")
    id("com.github.ben-manes.versions") version ("0.36.0")
}

val projectGroupId: String by project
val projectVersion: String by project

group = projectGroupId
version = projectVersion

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

fun getBuildProperty(property: String): String {
    val prop: String? = project.findProperty(property) as String?
    if(prop != null) {
        return prop
    }

    val env: String? = System.getenv(property)
    if (env != null) {
        return env
    }

    return "n/a"
}

fun getBuildSignKey(property: String): String {
    val prop: String? = project.findProperty(property) as String?
    if(prop != null) {
        return prop
    }

    val env: String? = System.getenv(property)
    if (env != null) {
        return env.replace("\\n", "\n")
    }

    return "n/a"
}

fun isReleaseVersion(): Boolean {
    return !(project.version.toString().endsWith("SNAPSHOT"))
}

project.ext {
    set("processorApiVersion", "2021.1")

    set("publishUser", getBuildProperty("PUBLISH_USER"))
    set("publishKey", getBuildProperty("PUBLISH_KEY"))
    set("signKey", getBuildSignKey("SIGN_KEY"))
    set("signPwd", getBuildProperty("SIGN_PWD"))
}


repositories {
    mavenCentral()
    maven {
        setUrl("https://oss.sonatype.org/content/repositories/snapshots")
        mavenContent {
            snapshotsOnly()
        }
    }
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.swagger.parser.v3:swagger-parser:2.0.24") {
        exclude(group = "io.swagger.parser.v3", module = "swagger-parser-v2-converter")
        exclude(group = "io.swagger.core.v3", module = "swagger-annotations")
    }
    compileOnly("io.openapiprocessor:openapi-processor-api:${project.ext.get("processorApiVersion")}")

    testImplementation("net.bytebuddy:byte-buddy:1.10.20")
    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5") {
        exclude(group = "org.codehaus.groovy", module = "groovy-json")
        exclude(group = "org.codehaus.groovy", module = "groovy-macro")
        exclude(group = "org.codehaus.groovy", module = "groovy-sql")
        exclude(group = "org.codehaus.groovy", module = "groovy-templates")
        exclude(group = "org.codehaus.groovy", module = "groovy-xml")
    }
    testImplementation("io.github.java-diff-utils:java-diff-utils:4.9")
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

// does not work on oss.sonatype.org
tasks.withType<GenerateModuleMetadata>().configureEach {
    enabled = false
}

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
                description.set(projectDesc)
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
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
            url = uri(if (isReleaseVersion()) releasesRepoUrl else snapshotsRepoUrl)

            credentials {
                username = project.extra["publishUser"].toString()
                password = project.extra["publishKey"].toString()
            }
        }
    }
}

//tasks.withType<Sign>().configureEach {
//    onlyIf { isReleaseVersion() }
//}

signing {
    useInMemoryPgpKeys(
        project.extra["signKey"].toString(),
        project.extra["signPwd"].toString())

    sign(publishing.publications["OpenApiProcessor"])
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
    return version.toString().endsWith("-SNAPSHOT")
}
