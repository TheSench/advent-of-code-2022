plugins {
    kotlin("jvm") version "1.7.22"
}

repositories {
    mavenCentral()
}

tasks {
    sourceSets {
        main {
            resources.setSrcDirs(listOf("src/main/resources"))
        }
    }

    wrapper {
        gradleVersion = "7.6"
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}