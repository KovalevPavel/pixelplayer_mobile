# Architecture Overview

Top-level structure:

- `androidApp`: Android shell and platform modules
- `composeApp`: shared app host, root navigation, and DI bootstrap
- `iosApp`: iOS host project

Shared vs platform-specific code:

- business logic and most UI live in `commonMain`
- platform bindings live in `androidMain` and `iosMain`
- playback and language behavior are adapted per platform where needed

Navigation shape:

- startup begins from `Initializer`
- if saved credentials exist, the app opens the authenticated main flow
- otherwise it opens the login flow

Architectural principles:

- modular feature boundaries
- typed navigation routes
- Koin-based dependency injection
- view models driven by state, actions, and events

Related pages:

- [Module Guide](Module-Guide)
- [Features](Features)
