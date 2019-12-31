plugins {
    groovy
    id("org.jetbrains.kotlin.jvm") version("1.3.60")
    id("maven-publish")
    id("com.jfrog.bintray") version ("1.8.4")
}

group = "com.github.hauner.openapi"
version = "1.0.0.M1"

repositories {
    mavenCentral()
    maven {
        setUrl("https://dl.bintray.com/hauner/openapi-generatr")
    }
}

project.ext {
    set("generatrApiVersion", "1.0.0.B2")

    set("bintrayUser", project.property("BINTRAY_USER") ?: "n/a")
    set("bintrayKey", project.property("BINTRAY_KEY") ?: "n/a")
}

dependencies {
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(kotlin("stdlib"))

    implementation("io.swagger.parser.v3:swagger-parser:2.0.12")
    compileOnly("com.github.hauner.openapi:openapi-generatr-api:${project.ext.get("generatrApiVersion")}")

    testImplementation("net.bytebuddy:byte-buddy:1.9.13")
    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")
    testImplementation("io.github.java-diff-utils:java-diff-utils:4.0")
    testImplementation("com.github.hauner.openapi:openapi-generatr-api:${project.ext.get("generatrApiVersion")}")
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

    setPublications("GeneratrJson")

    pkg.apply {
        repo = "openapi-generatr"
        name = "openapi-generatr-json"
        //userOrg = 'openapi-generatr'
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/hauner/openapi-generatr-json"

        version.apply {
            name = project.version.toString()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("GeneratrJson") {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}
