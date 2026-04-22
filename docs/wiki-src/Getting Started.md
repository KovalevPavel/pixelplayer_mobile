# Getting Started

Requirements:

- JDK 21
- Android Studio with Kotlin Multiplatform support
- Android SDK configured locally
- Xcode for iOS host development

Useful commands:

```bash
./gradlew :androidApp:assembleDebug
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

Android startup:

- open the repository in Android Studio
- use the `androidApp` configuration
- build or run the debug variant

iOS startup:

- open the `iosApp` host project in Xcode
- use the shared entry point from `composeApp/src/iosMain/kotlin/kovp/pixelplayer/MainViewController.kt`

Next steps:

- read [Architecture Overview](Architecture Overview.md)
- review [Module Guide](Module Guide.md)
