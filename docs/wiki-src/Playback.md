# Playback

Playback is centered in `core_player`, which exposes a platform-agnostic player abstraction and shared player state for the UI.

Android implementation:

- uses Media3 / ExoPlayer
- builds media items from track metadata
- updates player state from the active media controller
- streams tracks from `$baseUrl/api/play/{trackId}`

Supported actions:

- play by id
- play by index
- resume
- pause
- next
- previous
- seek
- clear player state on logout or endpoint change

UI behavior:

- the shared player UI reads `PlayerVs`
- it supports compact and expanded presentation
- it exposes timeline progress and current metadata
