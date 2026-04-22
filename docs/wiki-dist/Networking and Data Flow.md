> [Home](Home) | [Getting Started](Getting-Started) | [Architecture Overview](Architecture-Overview) | [Features](Features)

# Networking and Data Flow

The `network` module provides shared Ktor configuration and HTTP helper functions. The app uses authorized and unauthorized clients through dependency injection.

Expected backend route types in the current implementation:

- validation and login routes
- catalog routes for artists, albums, and tracks
- playback route for streaming track audio

Current examples from the repository:

- `validate`
- `login`
- `artists/all`
- `artists/get`
- `albums/all`
- `albums/get`
- `tracks/all`
- `/api/play/{trackId}`

Data flow:

- feature repositories call Ktor helpers
- DTOs stay inside implementation modules
- repositories map DTOs directly into `domain_*` value objects
- UI and view models consume only mapped domain data
