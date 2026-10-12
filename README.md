# Notion Life Widget

An Android home-screen widget for a compact view of your Notion life dashboard: tasks, goals, habits, and a read-only calendar.

## Status

**Starter scaffold.** The Android app and widget foundation are being added first. Live Notion sync and deployment require a server-side Notion integration token and your database/page IDs.

## Architecture

- **Android app:** Kotlin + Android AppWidget using Jetpack Glance.
- **Backend (planned):** a small authenticated proxy that calls the Notion API. Keep the Notion integration secret on the server, never in the APK.
- **Widget:** displays a short daily snapshot and refreshes through the app/backend.
- **Calendar:** read-only by design.

## Build

Open this repository in Android Studio (recent stable version), let Gradle sync, then run the `app` configuration on an Android device or emulator.

Requirements: JDK 17 and Android SDK 35.

## Notion setup still needed

1. Create a Notion internal integration and grant it access to the relevant pages/databases.
2. Identify the data sources for tasks, goals, habits, and calendar.
3. Deploy the backend and store the Notion token as a server secret.
4. Configure the app with the backend URL.

Do not commit API tokens or private Notion data.

## Next milestones

- [ ] Configure the secure Notion proxy
- [ ] Map the user's Notion properties and database IDs
- [ ] Implement sync, caching, and error states
- [ ] Add satisfying habit/task completion interactions
- [ ] Add a read-only calendar view
- [ ] Test widget refresh and Android battery/background limits
- [ ] Deploy backend and produce an installable APK
