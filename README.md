# NASA Gallery

A mobile app for browsing NASA's publicly available APIs, built with Kotlin Multiplatform for shared business logic and native UI — SwiftUI on iOS, Jetpack Compose on Android.

## Features

- **Astronomy Picture of the Day (APOD)** — daily image with description, sharing and favorites
- **Gallery** — browse and search the NASA Image Library with pagination
- **Favorites** — save images locally via SQLDelight with reactive Flow
- **Authentication** — Google Sign-In, Sign in with Apple, or continue as guest

## Tech Stack

| Area | Technology |
|------|------------|
| Shared logic | Kotlin Multiplatform |
| iOS UI | SwiftUI |
| Android UI | Jetpack Compose |
| Networking | Ktor |
| DI | Koin (shared/Android), Factory (iOS) |
| Database | SQLDelight |
| Auth | Firebase Auth |

## Architecture

Clean Architecture + MVI. Shared code covers the Model, Domain, Data and Presentation layers. Native UI and navigation are implemented separately on each platform.

```
shared/
  base/        BaseScopedViewModel, Result, HttpClient, Koin base
  auth/        Firebase Auth, Google + Apple Sign-In, Guest mode
  network/     NasaApiClient, AppConfig, DTOs
  database/    SQLDelight — Favorites, ApodCache
  apod/        APOD feature
  gallery/     Image library + pagination
  search/      Debounced search + suggestions
  favorites/   SQLDelight CRUD, reactive Flow
  umbrella/    Aggregates all modules → iOS xcframework

android/       Jetpack Compose screens, NavHost
ios/           SwiftUI screens, design system, TabView
```

## NASA APIs

| API | Base URL |
|-----|----------|
| APOD | `https://api.nasa.gov/planetary/apod` |
| Images Search | `https://images-api.nasa.gov/search` |
| Images Asset | `https://images-api.nasa.gov/asset/{nasa_id}` |

The API key is configured via `local.properties` (Android) and `xcconfig` (iOS) — not included in the repository.

## Build

### Android
```bash
./gradlew :android:app:assembleAlphaDebug
```

### iOS
```bash
# 1. Build the KMP xcframework
./gradlew :shared:umbrella:assembleKMPSharedXCFramework

# 2. Copy to ios/DomainLayer/
cp -R shared/umbrella/build/XCFrameworks/Debug/KMPShared.xcframework ios/DomainLayer/KMPShared.xcframework

# 3. Open in Xcode
open ios/NASAGallery.xcodeproj
```

## Navigation

Three tabs:
- **Today** — APOD, daily image + calendar
- **Explore** — gallery + search
- **Saved** — favorites

## License

MIT
