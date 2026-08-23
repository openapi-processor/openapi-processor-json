plugins {
  id("io.github.ben-manes.versions.settings") version "0.59.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots")
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}

rootProject.name = "openapi-processor-json"
