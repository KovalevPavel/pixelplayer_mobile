# Pixelplayer

Pixelplayer is a Kotlin Multiplatform music player with a shared Compose Multiplatform UI for Android and iOS. The app connects to a remote media server, restores the saved session on startup, loads artists, albums, and tracks, and provides built-in playback controls from the shared UI layer.

## Try the Demo

Want to evaluate the app quickly? Download the demo build from [GitHub Releases](https://github.com/KovalevPavel/pixelplayer_mobile/releases) and connect it to the public demo server:

- Server URL: `89.124.108.196`
- Login: `test`
- Password: `test`

Detailed project documentation is published in the GitHub Wiki.

## Key Features

- server endpoint validation and user login
- session restore with saved endpoint and token
- library browsing for artists, albums, and tracks
- artist and album detail screens
- built-in audio player with play, pause, previous, next, and seek
- settings for endpoint, logout, and app language

## Platforms

- Android host: `androidApp`
- Shared app and UI: `composeApp`
- iOS host: `iosApp`

## Tech Stack

- Kotlin Multiplatform
- Compose Multiplatform
- Koin
- Ktor
- DataStore
- Media3 / ExoPlayer on Android

## Requirements

- JDK 21
- Android Studio
- Android SDK
- Xcode for iOS development

## Quick Start

```bash
./gradlew :androidApp:assembleDebug
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

## Documentation

- [GitHub Wiki Home](https://github.com/KovalevPavel/pixelplayer_mobile/wiki)
- [Getting Started](https://github.com/KovalevPavel/pixelplayer_mobile/wiki/Getting-Started)
- [Architecture Overview](https://github.com/KovalevPavel/pixelplayer_mobile/wiki/Architecture-Overview)

The wiki source files live in [docs/wiki-src](/docs/wiki-src), and the generated publish-ready pages live in [docs/wiki-dist](docs/wiki-dist).
