> [Home](Home) | [Getting Started](Getting-Started) | [Architecture Overview](Architecture-Overview) | [Features](Features)

# Module Guide

Main module groups:

- `core_*`: shared infrastructure and reusable foundations
- `api_*`: public wrappers, routes, and DI entry points
- `domain_*`: contracts and value objects
- `feature_*`: screen logic, UI, and repository implementations

Key modules:

- `core`: shared helpers, context, build config, and language primitives
- `core_design`: theme, typography, colors, previews
- `core_ui`: reusable components and view model helpers
- `network`: Ktor setup and HTTP helpers
- `core_player`: playback abstraction and bindings
- `core_storage`: preferences storage
- `core_credentials`: credentials persistence
- `core_main_flow`: authenticated flow support objects

Dependency rules:

- prefer depending on `api_*` instead of another feature's internal module
- keep `domain_*` lightweight and mostly infrastructure-free
- add shared UI to `core_ui`, not to feature modules
- keep DTO-to-domain mapping inside feature implementation modules

Choosing where to add code:

- new cross-cutting helper: `core` or another `core_*`
- public navigation or wrapper API: `api_*`
- pure business contract or value object: `domain_*`
- screen, view model, repository implementation: `feature_*`
