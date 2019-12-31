plugins {
    groovy
    id("org.jetbrains.kotlin.jvm") version("1.3.60")
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
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("io.swagger.parser.v3:swagger-parser:2.0.12")
    compileOnly("com.github.hauner.openapi:openapi-generatr-api:${project.ext.get("generatrApiVersion")}")

    testImplementation("net.bytebuddy:byte-buddy:1.9.13")
    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")
    testImplementation("io.github.java-diff-utils:java-diff-utils:4.0")
    testImplementation("com.github.hauner.openapi:openapi-generatr-api:${project.ext.get("generatrApiVersion")}")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.compileGroovy {
    dependsOn("compileKotlin")
    classpath += files(tasks.compileKotlin.get().destinationDir)
}

tasks.compileTestGroovy {
    dependsOn("compileKotlin")
    classpath += files(tasks.compileKotlin.get().destinationDir)
}
