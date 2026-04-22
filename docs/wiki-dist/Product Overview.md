> [Home](Home) | [Getting Started](Getting-Started) | [Architecture Overview](Architecture-Overview) | [Features](Features)

# Product Overview

Pixelplayer is a client application for browsing and playing music from a remote media server. The current implementation focuses on a compact core flow: authenticate, restore session, open the catalog, and control playback from the shared UI.

Main user scenarios:

- enter a server endpoint and validate it
- sign in and persist the session
- browse artists, albums, and tracks
- open artist and album details
- start playback and control the current track
- update endpoint, switch language, or log out

Current capabilities:

- login and session restore
- artists, albums, and tracks catalog
- album track metadata including duration and quality
- Android playback integration
- language selection and endpoint management

Current limitations:

- language override is available on Android, while iOS currently resolves to English without in-app override
- the repository already contains an iOS host, but Android has the more explicit playback implementation in the current codebase
