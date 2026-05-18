# NASA Gallery

Mobilní aplikace pro prohlížení obsahu z NASA volně dostupných API. Postavená na Kotlin Multiplatform se sdílenou business logikou a nativním UI — SwiftUI na iOS, Jetpack Compose na Android.

## Funkce

- **Dnešní snímek (APOD)** — astronomy picture of the day s popisem, sdílením a oblíbenými
- **Galerie** — procházení a vyhledávání v NASA Image Library s podporou stránkování
- **Oblíbené** — ukládání snímků lokálně přes SQLDelight, reaktivní Flow
- **Přihlášení** — Google Sign-In, Sign in with Apple, nebo pokračovat jako host

## Technologie

| Oblast | Technologie |
|--------|-------------|
| Sdílená logika | Kotlin Multiplatform |
| iOS UI | SwiftUI |
| Android UI | Jetpack Compose |
| Networking | Ktor |
| DI | Koin (shared/Android), Factory (iOS) |
| Databáze | SQLDelight |
| Auth | Firebase Auth |

## Architektura

Clean Architecture + MVI. Sdílený kód pokrývá Model, Domain, Data a Presentation vrstvy. Nativní UI a navigace zůstávají na každé platformě zvlášť.

```
shared/
  base/        BaseScopedViewModel, Result, HttpClient, Koin base
  auth/        Firebase Auth, Google + Apple Sign-In, Guest mode
  network/     NasaApiClient, AppConfig, DTOs
  database/    SQLDelight — Favorites, ApodCache
  apod/        APOD feature
  gallery/     Image library + pagination
  search/      Debounced search + suggestions
  favorites/   SQLDelight CRUD, reaktivní Flow
  umbrella/    Agreguje všechny moduly → iOS xcframework

android/       Jetpack Compose screeny, NavHost
ios/           SwiftUI screeny, design system, TabView
```

## NASA API

| API | Base URL |
|-----|----------|
| APOD | `https://api.nasa.gov/planetary/apod` |
| Images Search | `https://images-api.nasa.gov/search` |
| Images Asset | `https://images-api.nasa.gov/asset/{nasa_id}` |

API klíč se konfiguruje v `local.properties` (Android) a `xcconfig` (iOS) — není součástí repozitáře.

## Build

### Android
```bash
./gradlew :android:app:assembleAlphaDebug
```

### iOS
```bash
# 1. Sestavit KMP xcframework
./gradlew :shared:umbrella:assembleKMPSharedXCFramework

# 2. Zkopírovat do ios/DomainLayer/
cp -R shared/umbrella/build/XCFrameworks/Debug/KMPShared.xcframework ios/DomainLayer/KMPShared.xcframework

# 3. Otevřít v Xcode
open ios/NASAGallery.xcodeproj
```

## Navigace

Tři taby:
- **Today** — APOD, dnešní snímek + kalendář
- **Explore** — galerie + vyhledávání
- **Saved** — oblíbené

## Licence

MIT
