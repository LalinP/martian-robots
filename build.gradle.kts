plugins {
    kotlin("jvm") version "2.4.21"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:6.2.7")
    testImplementation("io.kotest:kotest-assertions-core:6.2.7")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}
