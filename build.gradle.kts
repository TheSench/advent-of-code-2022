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
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
}

tasks.test {
  useJUnitPlatform()
}