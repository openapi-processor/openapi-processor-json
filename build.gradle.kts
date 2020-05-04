plugins {
    groovy
    id("org.jetbrains.kotlin.jvm") version("1.3.72")
    id("maven-publish")
    id("com.jfrog.bintray") version ("1.8.5")
    id("com.github.ben-manes.versions") version ("0.28.0")
}

group = "com.github.hauner.openapi"
version = "1.0.0.M3"

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://dl.bintray.com/hauner/openapi-processor")
    }
}

project.ext {
    set("processorApiVersion", "1.0.0.M4")

    set("bintrayUser", project.findProperty("BINTRAY_USER") ?: "n/a")
    set("bintrayKey", project.findProperty("BINTRAY_KEY") ?: "n/a")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.swagger.parser.v3:swagger-parser:2.0.19") {
        exclude(group = "io.swagger.parser.v3", module = "swagger-parser-v2-converter")
        exclude(group = "io.swagger.core.v3", module = "swagger-annotations")
    }
    compileOnly("com.github.hauner.openapi:openapi-processor-api:${project.ext.get("processorApiVersion")}")

    testImplementation("net.bytebuddy:byte-buddy:1.10.10")
    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5") {
        exclude(group = "org.codehaus.groovy", module = "groovy-json")
        exclude(group = "org.codehaus.groovy", module = "groovy-macro")
        exclude(group = "org.codehaus.groovy", module = "groovy-sql")
        exclude(group = "org.codehaus.groovy", module = "groovy-templates")
        exclude(group = "org.codehaus.groovy", module = "groovy-xml")
    }
    testImplementation("io.github.java-diff-utils:java-diff-utils:4.7")
    testImplementation("com.github.hauner.openapi:openapi-processor-api:${project.ext.get("processorApiVersion")}")
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

bintray {
    user = project.ext.get("bintrayUser").toString()
    key = project.ext.get("bintrayKey").toString()

    setPublications("processor")

    pkg.apply {
        repo = "openapi-processor"
        name = "openapi-processor-json"
        //userOrg = 'openapi-generatr'
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/hauner/openapi-processor-json"

        version.apply {
            name = project.version.toString()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("processor") {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}
