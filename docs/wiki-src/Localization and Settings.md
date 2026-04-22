# Localization and Settings

The settings flow is responsible for account-level and application-level adjustments.

Settings capabilities:

- show saved username
- show current endpoint
- change endpoint
- log out
- change app language

Supported languages:

- English
- Russian
- German

Language behavior:

- language selection is stored through `AppLanguageRepository`
- `LanguageSelection.System` clears the saved override
- Android supports applying a concrete language override
- iOS currently reports English and does not expose in-app override behavior

Logout and endpoint change behavior:

- clear the player state
- clear saved user data
- return the user to the login flow
