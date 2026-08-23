@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE")

plugins {
    java
    groovy
    kotlin
}

testing {
    suites {
        getByName<JvmTestSuite>("test") {
            useJUnitJupiter()
        }
    }
}
