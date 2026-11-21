# martian-robots
# BPL Take Home Test : Lalin Pethiyagoda

A Kotlin/JVM command-line application built with Gradle.

## Requirements

- JDK 25. If it isn't installed, Gradle downloads a matching JDK automatically (via the foojay toolchain resolver).
- No local Gradle install is needed: use the included Gradle wrapper (`./gradlew`).

## Getting started

```bash
./gradlew build   # compile and run tests
./gradlew test    # run tests only
./gradlew run     # run the application
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Project structure

```
martian-robots/
├── build.gradle.kts              # Build configuration (plugins, dependencies, toolchain)
├── settings.gradle.kts           # Project name and settings plugins
├── gradle.properties             # Gradle build properties
├── gradle/
│   ├── libs.versions.toml        # Version catalog: all dependency and plugin versions
│   └── wrapper/                  # Gradle wrapper (pins Gradle 9.4.0)
├── gradlew, gradlew.bat          # Gradle wrapper scripts
└── src/
    ├── main/kotlin/com/bpl/martianrobots/
    │   └── Main.kt               # Application entry point
    └── test/kotlin/com/bpl/martianrobots/
        └── MainTest.kt           # Unit tests
```

## Configuration

### Toolchain

| Component | Version |
|-----------|---------|
| Kotlin    | 2.3.0   |
| JDK       | 25      |
| Gradle    | 9.4.0 (wrapper) |
| JUnit     | 6.0.1 (Jupiter) |

The JDK version is set in `build.gradle.kts` with `kotlin { jvmToolchain(25) }`.

### Application

The `application` plugin is configured with the main class `com.bpl.martianrobots.MainKt` (the `main` function in `Main.kt`), so `./gradlew run` starts the app.

### Gradle properties

`gradle.properties` enables the [configuration cache](https://docs.gradle.org/current/userguide/configuration_cache.html) (`org.gradle.configuration-cache=true`) for faster repeat builds.

## Dependency management

All dependencies and plugin versions are managed in the version catalog at `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "2.3.0"
junit-jupiter = "6.0.1"

[libraries]
junit-jupiter = { module = "org.junit.jupiter:junit-jupiter", version.ref = "junit-jupiter" }
junit-platform-launcher = { module = "org.junit.platform:junit-platform-launcher", version.ref = "junit-jupiter" }
kotlin-test-junit5 = { module = "org.jetbrains.kotlin:kotlin-test-junit5", version.ref = "kotlin" }

[plugins]
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
```

`build.gradle.kts` refers to catalog entries through the generated `libs` accessor and contains no hard-coded versions.

### Adding a dependency

1. Add the version under `[versions]` and the library under `[libraries]` in `gradle/libs.versions.toml`:
   ```toml
   [versions]
   kotlinx-coroutines = "<version>"

   [libraries]
   kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinx-coroutines" }
   ```
2. Refer to it in `build.gradle.kts`. Dashes in the alias become dots:
   ```kotlin
   dependencies {
       implementation(libs.kotlinx.coroutines.core)
   }
   ```

To upgrade a library, change its version in `[versions]`. Every entry that uses that `version.ref` picks up the change.

The foojay toolchain resolver plugin in `settings.gradle.kts` is the one exception: Gradle reads the settings file before the catalog is available, so its version is declared there directly.

## IDE setup (IntelliJ IDEA)

Open the project folder and load it as a Gradle project (**Load Gradle Changes**, or **Reload All Gradle Projects** in the Gradle tool window). IntelliJ then picks up the Kotlin source folders, the JDK toolchain and the `libs` catalog accessors.
