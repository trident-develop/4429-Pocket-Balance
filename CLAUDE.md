# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PocketBalance is an Android app built with Kotlin and Jetpack Compose (Material 3). Single-module project, early stage. Package namespace: `co.imba`.

## Build & Test Commands

```bash
# Build
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew testDebugUnitTest --tests "co.imba.ExampleUnitTest"

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean assembleDebug
```

## Tech Stack

- **Kotlin** 2.0.21, Java 11 target
- **Jetpack Compose** (BOM 2024.09.00) with Material 3
- **Gradle** 9.2.1 with Kotlin DSL and version catalog (`gradle/libs.versions.toml`)
- **Min SDK** 28, **Target/Compile SDK** 36
- **AGP** 9.0.1

## Code Structure

- `app/src/main/java/co/imba/` — main source code
  - `MainActivity.kt` — single activity entry point (Compose, edge-to-edge)
  - `ui/theme/` — Material 3 theme (colors, typography, dynamic color support on Android 12+)
- `app/src/test/` — local unit tests (JUnit 4)
- `app/src/androidTest/` — instrumented tests (Espresso, Compose test)
