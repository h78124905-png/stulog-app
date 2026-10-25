# StulogPlus

StulogPlus is an offline-first Android study planner and focus timer built with Kotlin, Jetpack Compose, Navigation Compose, and Room.

## Features
- Focus timer with start, pause, reset, and saved study sessions
- Daily task list with completion toggles and deletion
- Subject list with pastel color markers
- Daily study-time summary and session history
- Local Room database; no account or network permission required
- Light/dark color schemes following system appearance

## Build
Open this repository in Android Studio (JDK 17), or run:

```bash
gradle assembleDebug
```

The GitHub Actions workflow builds the debug APK on pushes and pull requests.

## Notes
The project uses Android Gradle Plugin 8.2.2, Kotlin 1.9.22, Compose Compiler 1.5.8, and Gradle 8.2. The Gradle wrapper scripts and wrapper JAR are not committed yet; Android Studio can import the project or a local Gradle 8.2 installation can build it.
