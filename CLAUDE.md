# NASA Gallery — KMP App

Mobilní aplikace (iOS + Android) pro prohlížení obsahu z NASA free API. Kotlin Multiplatform (sdílená business logika) + nativní UI (SwiftUI na iOS, Compose na Android).

## NASA API

| API | Base URL | Auth |
|---|---|---|
| APOD | `https://api.nasa.gov/planetary/apod` | `?api_key=<KEY>` |
| Images Search | `https://images-api.nasa.gov/search` | žádná |
| Images Asset | `https://images-api.nasa.gov/asset/{nasa_id}` | žádná |

API key: uložen v `local.properties` (Android) + `xcconfig` (iOS), **nikdy necommitovat**.

## Architektura

```
shared/
  base/        BaseScopedViewModel<S,I,E>, Result<T>, HttpClient, Koin base
  auth/        Firebase Auth, Google + Apple Sign-In, Guest mode
  network/     NasaApiClient (apodClient + imagesClient), AppConfig, DTOs
  database/    SQLDelight — Favorite.sq, ApodCache.sq
  apod/        APOD feature (Service → UseCase → ViewModel)
  gallery/     Image library + pagination
  search/      Debounced search + suggestions
  favorites/   SQLDelight CRUD, reaktivní Flow
  umbrella/    Agreguje všechny moduly, generuje iOS xcframework

android/       Nativní Compose screeny, Credential Manager, NavHost
ios/           Nativní SwiftUI screeny, design system, TabView
```

### Clean architecture (Matee)

Referenční wiki: [Common clean architecture](https://mateedevs.notion.site/Common-clean-architecture-c0e982559d1e4af0aa41a07e6bec499f) (v1.0.0). Níže mapování na tento repozitář — **závislosti směřují dovnitř** (System → Presentation → Domain → Model; Data implementuje Domain; DI propojuje moduly).

| Vrstva (Matee) | Obsah | Kde v NASA Gallery |
|----------------|--------|----------------------|
| **Model** | Business entity, enumy, bez platformního SDK | `shared/<feature>/domain/model/`, čisté modely bez frameworků |
| **Domain** | Use cases `*UseCase`, rozhraní `*Repository` / `*Controller` | `domain/usecase/`, kontrakty repository v domain |
| **Data** | Implementace repository, entity DB/sítě, zdroje dat | `data/repository/`, `remote/`, `local/`, `dto/` |
| **Presentation** | ViewModely, stav, VO (mapování výsledků UC pro UI) | `presentation/*ViewModel`, `*State`, `*Intent`, `*Event` |
| **Device** | Implementace controllerů (ne data: oprávnění, notifikace, systémové API) | `androidMain` / `iosMain` v feature, např. auth / platform bridge |
| **System** | Konkrétní UI a lifecycle | Compose / SwiftUI (`*FeatureView`), `Application`, `AppDelegate`; SPM modul **`NasaGalleryRoot`** (shell: tab bar + `RootCoordinator`) — nesmí mít stejný název modulu jako iOS app target `NasaGallery` |
| **DI** | Propojení závislostí | Koin `di/*Module.kt`, iOS `Application/DependencyInjection` + Factory |
| **Extensions** | Čisté rozšíření jazyka / malé helpery | `DomainLayer/Utilities`, případně `shared/base` — ne business logika |

**KMP pravidlo:** sdílený kód drží Model + Domain + Data + Presentation co nejvíc v `commonMain`; System a část Device zůstávají v `androidApp` / iOS targetech.

## Navigace (3 taby)

| Tab | Obsah |
|---|---|
| Today | APOD — dnešní snímek + kalendář |
| Explore | Gallery + Search (unified) |
| Saved | Favorites |

## Design systém (iOS)

Tokeny jako Swift extensions:
- `CGFloat` — `.spaceXS/SM/MD/LG/XL/XXL`, `.radiusSM/MD/Card/Button/Full`
- `Color` — `.nasaPrimary`, `.nasaBackground`, `.nasaSurface`, `.nasaOnSurface`, `.nasaAccent`, `.nasaSubtle`
- `Font` — `.nasaDisplay`, `.nasaHeadline`, `.nasaTitle`, `.nasaBody`, `.nasaCaption`

Komponenty: Atom → Molecule → Organism (viz `ios/NasaGallery/DesignSystem/`)

## Coding guidelines

### KMP (Kotlin)
- MVI: `State` (immutable data class), `Intent` (sealed interface), `Event` (sealed interface)
- Každý use case = 1 soubor, implementuje příslušné `UseCase*` rozhraní z `shared/base`
- Chyby: `Result<T>` sealed class, nikdy nevrhej exception mimo Data layer
- DI: Koin modul v každém feature modulu, `umbrella` agreguje vše
- Pattern: `Service → Source → Repository → UseCase → ViewModel`

### SwiftUI (iOS) — Pattern B
- **Jeden `FeatureView.swift` na feature** — žádné `Screen.swift` soubory
- FeatureView = VM bridge + kompletní UI v jednom souboru
- `@Injected(\.featureViewModel) private var viewModel: FeatureViewModel`
- State: `@State private var state = FeatureState()` + `.task { for await s in viewModel.state { state = s } }`
- Events: `.bindViewModel(viewModel, onEvent: onEvent)`
- Vždy `.nasaXX` tokeny, nikdy hardcoded čísla/barvy
- Komponenty v `UIToolkit/` jsou stateless, parametrizované (Atom → Molecule → Organism)
- **View decomposition**: `body` je čistá kostra, každý vizuální blok = vlastní `private var`.
  - `body` jen volá private properties, žádná inline logika
  - Vnořené komponenty specifické pro screen = `private struct` pod `// MARK: -` ve stejném souboru
  - UIToolkit atomy: NasaChip, NasaAsyncImage, FavoriteButton, NasaEmptyView, NasaLoadingView, NasaErrorView, ApodHeroCard, NasaLogoView, NasaSignInButton, NasaFavoriteRow, NasaUserProfileCard, NasaSettingsActionRow, NasaSettingsToggleRow, NasaSectionHeader

### Compose (Android)
- Screeny = stateless `@Composable`, dostávají `state` + `onIntent`
- `NasaTheme` wrapper, tokeny z `NasaSpacing`/`NasaRadius` objektů
- koinViewModel() pro získání VM v route composable
- **View decomposition**: stejný princip jako SwiftUI — každý vizuální blok = vlastní private `@Composable` funkce.
  - Hlavní screen composable volá jen private composables, neobsahuje inline UI logiku
  - Row/Card komponenty = vlastní private `@Composable` funkce pod `// region` nebo komentářem
  - Maximální využití shared komponent (NasaChip, NasaAsyncImage, FavoriteButton, NasaEmptyView, NasaLoadingView, NasaErrorView, ApodHeroCard)

## Struktura feature modulu (vzor z shared/samplefeature)

```
shared/<feature>/src/commonMain/kotlin/cz/tomasbrand/nasagallery/<feature>/
  data/
    remote/  <Feature>Service.kt + <Feature>ServiceImpl.kt
    dto/     <Feature>Dto.kt (@Serializable)
    local/   <Feature>Source.kt + <Feature>SourceImpl.kt (SQLDelight)
    repository/ <Feature>RepositoryImpl.kt
  domain/
    model/   <Feature>.kt (čistý model, bez anotací)
    usecase/ Get<Feature>UseCase.kt + impl
  presentation/
    <Feature>ViewModel.kt
    <Feature>State.kt
    <Feature>Intent.kt
    <Feature>Event.kt
  di/
    <Feature>Module.kt (Koin)
```

## Auth flow

```
App launch
  └── GetCurrentUserUseCase → null → AuthScreen
        ├── "Explore with Google" → SocialAuthProvider.signInWithGoogle() → Firebase → MainTabs
        ├── "Sign in with Apple" → SocialAuthProvider.signInWithApple() → Firebase → MainTabs
        └── "Continue as Guest" → SignInAsGuestUseCase → MainTabs (omezený přístup)
```

Guest může: browsovat Gallery, APOD. Nemůže: ukládat Favorites (→ modal sign-in sheet).

## MCP servery

- **Pencil MCP**: aktívní, design file: `pencil-new.pen`
- **Figma MCP**: aktivuj přes `claude mcp add figma -s project`
  - NASA Gallery file: `32OyUPY6rvNLgEpVgKWV92`
  - Style guide node: `8-677`
  - Screens node: `1-2042`

## Build

### Android
```bash
./gradlew :android:app:assembleAlphaDebug
```

### iOS
```bash
# 1. Sestavit KMP xcframework (vyžaduje JDK 21)
./gradlew :shared:umbrella:assembleKMPSharedXCFramework

# 2. Zkopírovat do ios/DomainLayer/
cp -R shared/umbrella/build/XCFrameworks/Debug/KMPShared.xcframework ios/DomainLayer/KMPShared.xcframework

# 3. Otevřít a buildovat v Xcode
open ios/NASAGallery.xcodeproj
# případně ios/NASAGallery.xcworkspace, pokud ho používáš
```

**Poznámka:** xcframework název je `KMPShared` (definováno v `ProjectConstants.iosShared`).
Po buildu pro Release použij `XCFrameworks/Release/KMPShared.xcframework`.

## Verze závislostí (z libs.versions.toml)

- Kotlin: 2.2.10 | KMP: 2.2.10
- Compose Multiplatform: 1.8.2
- Ktor: 3.2.3 | Koin: 4.1.1
- SKIE: 0.10.6 (Swift/Kotlin interop)
- SQLDelight: přidat do libs.versions.toml
