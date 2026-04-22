# Development Notes

Implementation patterns used in the repository:

- Koin-based dependency injection
- typed Navigation Compose routes
- action-driven view models
- small feature wrappers for navigation and DI boundaries

Good entry points when exploring the codebase:

- `composeApp/src/commonMain/kotlin/kovp/pixelplayer/App.kt`
- `composeApp/src/commonMain/kotlin/kovp/pixelplayer/initializer`
- `settings.gradle.kts`

Useful verification commands:

```bash
./gradlew :androidApp:assembleDebug
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

Documentation workflow:

- edit source files in `docs/wiki-src`
- run `python3 scripts/generate_wiki.py`
- review generated output in `docs/wiki-dist`
- let GitHub Actions publish the generated pages to GitHub Wiki
