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

plugins {
  id("io.github.ben-manes.versions.settings") version "0.64.0"
}

rootProject.name = "openapi-processor-json"
