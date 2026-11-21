plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    jvmToolchain(25)
}

application {
    mainClass = "com.martianrobots.MainKt"
}

tasks.test {
    useJUnitPlatform()
}
