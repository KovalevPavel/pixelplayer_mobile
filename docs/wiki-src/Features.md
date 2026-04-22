# Features

Authentication:

- validates endpoint before login
- saves endpoint, username, and token
- restores session on next startup

Artists:

- loads the full artist list
- opens artist details with related albums

Albums:

- loads the full album list
- opens album details with tracks, duration, disc number, and quality

Tracks:

- loads the global track catalog
- exposes title, album, artist, and cover metadata

Player:

- loads tracks into the player layer
- supports play, pause, previous, next, and seek
- exposes compact and expanded UI states

Settings:

- shows current username and endpoint
- supports endpoint change, logout, and language selection

Related pages:

- [Playback](Playback.md)
- [Localization and Settings](Localization and Settings.md)
