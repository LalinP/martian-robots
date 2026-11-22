# martian-robots
# BPL Take Home Test : Lalin Pethiyagoda

A Kotlin/JVM command-line solution to the Martian Robots problem, built with Gradle.

## The problem

Robots move around a rectangular grid on Mars. The lower-left corner is `0 0` and the input gives the upper-right corner. Each robot has a position (`x y`) and an orientation (`N`, `E`, `S` or `W`), and follows a string of instructions:

- `L`: turn left 90 degrees, staying on the same point
- `R`: turn right 90 degrees, staying on the same point
- `F`: move forward one point in the direction it is facing
- `B`: move back one point, opposite to the direction it is facing, without turning **(an extension to the original problem)**
- `J`: jump forward two points in the direction it is facing **(an extension to the original problem)**. Each point is checked separately, so a robot can be lost, or saved by a scent, on either step

A robot that moves off the grid, whether forwards, backwards or mid-jump, is **LOST**. It is reported at the last point it held on the grid, and it leaves a **scent** there. A later robot on a scented point ignores any move (`F`, `B` or a step of `J`) that would take it off the grid. Robots run one after another, so each robot only benefits from scents left by robots before it.

Limits: grid coordinates are at most 50, and instruction strings are fewer than 100 characters.

### Input and output

The first line is the upper-right corner of the grid. Each robot then takes two lines: its starting position and orientation, then its instructions. Blank lines are ignored.

```
5 3
1 1 E
RFRFRFRF

3 2 N
FRRFLLFFRRFLL

0 3 W
LLFFFLFLFL
```

The output has one line per robot, showing its final position and orientation, plus `LOST` if it fell off:

```
1 1 E
3 3 N LOST
2 3 S
```

Input rules:

- **One grid per file.** Only the first line is a grid size. Every line after it is read as a robot position or an instruction line, alternating. To run a second grid, put it in a separate file and run the app again.
- **Every robot needs both lines.** A position line must be followed by an instruction line.
- A position is `X Y O`, separated by spaces, and must be on the grid. `O` is one of `N`, `E`, `S`, `W`.
- Instructions contain only `L`, `R`, `F`, `B` and `J`, and are fewer than 100 characters.
- All robots in a file share the grid, so scents left by earlier robots apply to later ones.

Invalid input (bad format, coordinates outside the limits, unknown instructions) is reported on stderr with the line number, and the program exits with status 1.

## Requirements

- JDK 25. If it isn't installed, Gradle downloads a matching JDK automatically (via the foojay toolchain resolver).
- No local Gradle install is needed: use the included Gradle wrapper (`./gradlew`).

## Getting started

```bash
./gradlew build   # compile and run tests
./gradlew test    # run tests only
./gradlew run -q --args="sample-input.txt"   # run with an input file
./gradlew run -q < sample-input.txt          # or pipe input through stdin
```

`sample-input.txt` contains the example input shown above. Relative file paths are resolved from the project directory.

### Saving the output to a file

Redirect standard output to write the results to `output.txt` in the project directory:

```bash
./gradlew run -q --args="sample-input.txt" > output.txt
cat output.txt
```

For the sample input, `output.txt` contains:

```
1 1 E
3 3 N LOST
2 3 S
```

Only the results go to `output.txt`. Errors are printed to stderr, so they still appear in the terminal. Keep `-q`: without it, Gradle's own build messages are written into the file too. Running the command again overwrites the file; use `>>` instead of `>` to append.

On Windows, use `gradlew.bat` instead of `./gradlew`.

### Troubleshooting

| Symptom | Cause and fix |
|---------|---------------|
| `./gradlew run` on its own appears to hang, or fails with `Input is empty` | With no file argument, the app reads input from stdin. Pass a file with `--args="sample-input.txt"`, or pipe one in with `< sample-input.txt`. |
| `BUILD FAILED ... finished with non-zero exit value 1` | The app rejected the input. The line above it, starting with `Error:`, gives the reason and line number. Run with `-q` to hide Gradle's own output. |
| `robot position has no instruction line` | The robot lines don't pair up. Usually a robot is missing its instruction line, or there is a second grid line (for example, the sample pasted twice). The line number points to where the pairing ran out, which can be after the real mistake: look for the first line that is in the wrong place. |
| `expected robot position as 'X Y O'` | A line in a position slot isn't a valid position. This is often an instruction line one slot out of place. |

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
├── sample-input.txt              # Example mission input
└── src/
    ├── main/kotlin/com/bpl/martianrobots/
    │   ├── Main.kt               # Entry point: reads a file argument or stdin, prints results
    │   ├── MartianRobots.kt      # Runs each robot in turn on a shared grid
    │   ├── InputParser.kt        # Parses and validates input into a Mission
    │   ├── Grid.kt               # Grid bounds and scents
    │   ├── Robot.kt              # Robot state and output formatting
    │   ├── Position.kt           # x/y coordinates
    │   ├── Orientation.kt        # N/E/S/W with left/right turns
    │   └── Command.kt            # L/R/F/B/J commands and the command registry
    └── test/kotlin/com/bpl/martianrobots/
        ├── MartianRobotsTest.kt  # End-to-end tests, including the sample input
        ├── InputParserTest.kt    # Parsing and validation
        ├── CommandTest.kt        # Turning, forward/back movement, LOST and scent behaviour
        ├── JumpTest.kt           # Jump movement, including edge and scent cases
        └── OrientationTest.kt    # Turning
```

### Design

- `Robot`, `Position` and `Orientation` are immutable. Each command returns a new `Robot`.
- `Grid` is the only mutable state: it records scents, and every robot in a mission shares it.
- `F`, `B` and `J` share one movement function, so the LOST and scent rules behave the same for every move. `J` calls it once per step and stops as soon as the robot is lost.
- Commands implement the `Command` interface and are looked up by character in `Commands`. To add a new instruction type, implement `Command` and register it in `Commands`; the parser and simulation don't need to change.

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

The `application` plugin is configured with the main class `com.bpl.martianrobots.MainKt` (the `main` function in `Main.kt`), so `./gradlew run` starts the app. The `run` task forwards stdin to the app so input can be piped in.

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
