# Kotlin Multiplatform + SwiftUI — Kompletní průvodce pro vývojáře

> Tento průvodce tě provede architekturou, vzory a každodenním workflow KMP projektu **NASA Gallery**.
> Každá kapitola obsahuje vysvětlení, reálný kód z projektu, otázky k zamyšlení a cvičení.

---

## Obsah

1. [Co je Kotlin Multiplatform?](#1-co-je-kotlin-multiplatform)
2. [Struktura projektu](#2-struktura-projektu)
3. [Vrstvená architektura — Clean Architecture v KMP](#3-vrstvená-architektura)
4. [Data vrstva — Service, DTO, Repository](#4-data-vrstva)
5. [Domain vrstva — Model, UseCase, Result](#5-domain-vrstva)
6. [Presentation vrstva — MVI pattern](#6-presentation-vrstva--mvi)
7. [Dependency Injection s Koin](#7-dependency-injection-s-koin)
8. [SKIE — Swift/Kotlin interop](#8-skie--swiftkotlin-interop)
9. [iOS — Pattern B (SwiftUI + KMP ViewModel)](#9-ios--pattern-b)
10. [SQLDelight — Lokální databáze](#10-sqldelight)
11. [Jak přidat nový feature modul](#11-jak-přidat-nový-feature-modul)
12. [Kvízy a cvičení](#12-kvízy-a-cvičení)

---

## 1. Co je Kotlin Multiplatform?

### Základní myšlenka

Kotlin Multiplatform (KMP) ti umožňuje **napsat business logiku jednou v Kotlinu** a použít ji na více platformách — Android, iOS, web, desktop. UI zůstává nativní na každé platformě.

```
┌─────────────────────────────────────────────┐
│           Sdílený Kotlin kód (commonMain)   │
│   networking, use cases, view modely,       │
│   databáze, business rules                  │
└───────────────────┬─────────────────────────┘
                    │
         ┌──────────┴──────────┐
         ▼                     ▼
  ┌─────────────┐       ┌─────────────┐
  │   Android   │       │     iOS     │
  │  (Compose)  │       │  (SwiftUI)  │
  │  nativní UI │       │  nativní UI │
  └─────────────┘       └─────────────┘
```

### Klíčové pojmy

| Pojem | Popis |
|-------|-------|
| `commonMain` | Kód sdílený mezi všemi platformami |
| `androidMain` | Kód specifický pro Android |
| `iosMain` | Kód specifický pro iOS |
| `expect/actual` | Mechanismus pro platformově specifické implementace |
| xcframework | Zkompilovaný KMP kód jako iOS framework |
| SKIE | Plugin generující Swift API z Kotlin kódu |

### expect / actual — jak funguje

Tenhle mechanismus je srdce KMP. V `commonMain` definuješ **rozhraní** pomocí `expect`, každá platforma pak dodá `actual` implementaci.

```kotlin
// commonMain — co CHCEME, aby existovalo
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// androidMain — Android implementace
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(NasaDatabase.Schema, context, "nasa.db")
}

// iosMain — iOS implementace
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(NasaDatabase.Schema, "nasa.db")
}
```

> **Pamatuj:** `expect` = kontrakt. `actual` = implementace pro konkrétní platformu.

---

### Kvíz 1.1

**Otázka:** Proč je výhodné sdílet business logiku přes KMP místo toho, aby každá platforma měla vlastní implementaci?

<details>
<summary>Odpověď</summary>

- **Konzistence**: Bug opravíš na jednom místě a fix se projeví na obou platformách
- **Méně duplicitního kódu**: Use cases, validace, síťové volání — jednou
- **Jednodušší testování**: Píšeš testy jednou v `commonTest`
- **Rychlejší vývoj**: Nová feature = jeden backend, dva UI

</details>

---

## 2. Struktura projektu

```
NASAGallery/
├── shared/                  ← KMP sdílená logika
│   ├── base/                ← BaseScopedViewModel, Result, HttpClient, Koin base
│   ├── auth/                ← Firebase Auth, Google/Apple Sign-In
│   ├── network/             ← NasaApiClient, DTOs, AppConfig
│   ├── database/            ← SQLDelight schémata a factory
│   ├── apod/                ← APOD feature (kompletní modul)
│   ├── gallery/             ← Image library
│   ├── search/              ← Vyhledávání
│   ├── favorites/           ← Oblíbené položky
│   └── umbrella/            ← Agreguje vše → generuje iOS xcframework
│
├── android/                 ← Android Compose UI
│   └── app/src/main/
│
└── ios/                     ← iOS SwiftUI projekt
    ├── Application/         ← AppDelegate, DI, entry point
    ├── PresentationLayer/   ← Feature Views (SwiftUI)
    ├── DataLayer/           ← iOS-specific providers
    └── DomainLayer/         ← KMPShared.xcframework, Utilities
```

### Anatomy feature modulu (APOD jako vzor)

```
shared/apod/src/commonMain/kotlin/cz/tomasbrand/nasagallery/apod/
├── data/
│   ├── remote/
│   │   └── ApodService.kt          ← HTTP volání přes Ktor
│   ├── dto/
│   │   └── ApodDto.kt              ← @Serializable, 1:1 s API
│   ├── local/
│   │   └── ApodCacheSource.kt      ← SQLDelight operace
│   └── repository/
│       └── ApodRepositoryImpl.kt   ← orchestrace: cache → network
├── domain/
│   ├── model/
│   │   └── Apod.kt                 ← čistý doménový model
│   └── usecase/
│       └── GetTodayApodUseCase.kt  ← business logika
├── presentation/
│   └── ApodViewModel.kt            ← MVI: State + Intent + Event
└── di/
    └── ApodModule.kt               ← Koin registrace
```

---

## 3. Vrstvená architektura

Projekt používá **Clean Architecture** s těmito vrstvami:

```
Presentation Layer  (ViewModel → UI)
        ↕  jen přes UseCase
Domain Layer        (UseCase → Repository interface)
        ↕  jen přes Repository interface
Data Layer          (Repository → Service/Source)
        ↕  HTTP / SQLDelight
External            (NASA API, SQLite)
```

### Pravidla závislostí

- Vnější vrstvy znají vnitřní, **nikdy naopak**
- `ApodService` nezná `Apod` model — zná jen `ApodDto`
- `GetTodayApodUseCase` nezná `ApodService` — zná jen `ApodRepository`
- `ApodViewModel` nezná `ApodRepositoryImpl` — zná jen `GetTodayApodUseCase`

---

## 4. Data vrstva

### 4.1 DTO (Data Transfer Object)

DTO je přesná kopie JSON odpovědi z API. Označený `@Serializable` pro Ktor/kotlinx.serialization.

```kotlin
// shared/network/src/commonMain/kotlin/.../dto/ApodDto.kt

@Serializable
data class ApodDto(
    val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    @SerialName("hdurl") val hdUrl: String? = null,
    @SerialName("media_type") val mediaType: String,
    val copyright: String? = null,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
)
```

> **Pravidlo:** DTO nikdy neobsahuje business logiku. Je to pouze "obal" pro JSON.
> Nullable pole mají vždy defaultní hodnotu `= null`.

### 4.2 Service — HTTP volání

Service volá API a vrací `Result<Dto>`. Nic víc.

```kotlin
// shared/apod/.../data/remote/ApodService.kt

internal class ApodService(
    private val client: HttpClient,  // Ktor HttpClient z Koin
) {
    suspend fun getApod(date: String? = null): Result<ApodDto> =
        runCatchingCommonNetworkExceptions {
            client.get(NasaApiConstants.Endpoints.APOD) {
                if (date != null) parameter(NasaApiConstants.QueryParams.DATE, date)
            }.body()
        }
}
```

Klíčové body:
- `internal` — Service je implementační detail, neexponuje se ven z modulu
- `runCatchingCommonNetworkExceptions` — wrapper, který zachytí síťové chyby a převede je na `Result.Error`
- `.body()` — Ktor automaticky deserializuje JSON do `ApodDto`

### 4.3 Repository — orchestrace

Repository rozhoduje: použij cache nebo zavolej API.

```kotlin
// shared/apod/.../data/repository/ApodRepositoryImpl.kt

internal class ApodRepositoryImpl(
    private val service: ApodService,
    private val cache: ApodCacheSource,
) : ApodRepository {

    override suspend fun getToday(): Result<Apod> {
        // 1. Zkus cache
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        cache.getByDate(today)?.let { return Result.Success(it) }

        // 2. Zavolej API a ulož do cache
        return service.getApod(date = null).map { dto ->
            dto.toApod().also { cache.save(it) }
        }
    }
}

// Mapping DTO → Domain model (private extension funkce)
private fun ApodDto.toApod() = Apod(
    date = date,
    title = title,
    explanation = explanation,
    url = url,
    hdUrl = hdUrl,
    mediaType = MediaType.from(mediaType),
    copyright = copyright,
    thumbnailUrl = thumbnailUrl,
)
```

> **Proč je mapping v Repository?** Domain model neví o DTOs. Repository je jediné místo,
> kde se mapa "síťový formát → business objekt" smí dít.

---

### Kvíz 4.1

**Otázka:** Proč má `ApodRepositoryImpl` jako dependency `ApodRepository` interface
a ne přímo `ApodRepositoryImpl`? Co by se stalo, kdybychom použili `impl`?

<details>
<summary>Odpověď</summary>

- Use case závisí na **interface** `ApodRepository`, ne na implementaci
- Díky tomu můžeme v testech vyměnit `ApodRepositoryImpl` za `FakeApodRepository`
- Koin registruje: `singleOf(::ApodRepositoryImpl) bind ApodRepository::class`
- Toto je **Dependency Inversion Principle** (D v SOLID)

</details>

---

### Cvičení 4.1 — Přidej nový endpoint

Přidej do `ApodService` metodu `getRandomApod()` která zavolá APOD API s parametrem `count=1`.

```kotlin
// Hint — API endpoint:
// GET https://api.nasa.gov/planetary/apod?count=1
// Vrací List<ApodDto>

suspend fun getRandomApod(): Result<ApodDto> = // tvůj kód
```

<details>
<summary>Řešení</summary>

```kotlin
suspend fun getRandomApod(): Result<ApodDto> =
    runCatchingCommonNetworkExceptions {
        client.get(NasaApiConstants.Endpoints.APOD) {
            parameter("count", 1)
        }.body<List<ApodDto>>().first()
    }
```

</details>

---

## 5. Domain vrstva

### 5.1 Doménový model

Čistý Kotlin datový objekt — žádné anotace, žádné frameworky.

```kotlin
// shared/apod/.../domain/model/Apod.kt

data class Apod(
    val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdUrl: String?,
    val mediaType: MediaType,
    val copyright: String?,
    val thumbnailUrl: String?,
) {
    // Business logika jako computed properties
    val displayUrl: String get() = if (mediaType == MediaType.VIDEO) thumbnailUrl ?: url else url
    val isVideo: Boolean get() = mediaType == MediaType.VIDEO
}
```

> `data class` ti automaticky generuje `equals`, `hashCode`, `copy` a `toString`.
> Pro KMP je to zlatý standard pro doménové modely.

### 5.2 Result — typově bezpečné chyby

Projekt nikde nepoužívá `throw`/`try-catch` mimo Data vrstvu. Místo toho `Result<T>`:

```kotlin
// shared/base/.../domain/model/Result.kt

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error<out T : Any>(val error: ErrorResult, val data: T? = null) : Result<T>()
}
```

#### Práce s Result — extension funkce

```kotlin
// Reaguj na úspěch i chybu
getTodayApod()
    .alsoOnSuccess { apod -> this.apod = apod }
    .alsoOnError   { error -> this.error = error }

// Transformuj hodnotu uvnitř Success
service.getApod().map { dto -> dto.toApod() }

// Vyber hodnotu nebo fallback
val apod = result.getOrElse { defaultApod }

// Fold — zpracuj obě větve a vrať hodnotu
val title = result.fold(
    success = { it.title },
    error   = { "Načítání selhalo" }
)
```

### 5.3 UseCase

Každý use case = **jeden soubor, jedna operace**.

```kotlin
// Interface (veřejné API)
interface GetTodayApodUseCase : UseCaseResultNoParams<Apod>

// Implementace (internal — není vidět mimo modul)
internal class GetTodayApodUseCaseImpl(
    private val repository: ApodRepository,
) : GetTodayApodUseCase {
    override suspend fun invoke(): Result<Apod> = repository.getToday()
}
```

Základní typy use case rozhraní z `shared/base`:

| Interface | Popis |
|-----------|-------|
| `UseCaseResultNoParams<T>` | `suspend invoke(): Result<T>` — bez parametrů |
| `UseCaseResult<Params, T>` | `suspend invoke(params): Result<T>` — s parametry |
| `UseCaseFlowResultNoParams<T>` | `invoke(): Flow<Result<T>>` — reaktivní stream |

---

### Kvíz 5.1

**Otázka:** Proč je `GetTodayApodUseCaseImpl` označen jako `internal` ale `GetTodayApodUseCase` interface ne?

<details>
<summary>Odpověď</summary>

- **Interface** musí být viditelný zvenku — ViewModel ho dostane přes Koin
- **Implementace** je detail modulu — ViewModel neví, jak se data načítají
- `internal` = viditelné jen v rámci Gradle modulu
- Toto splňuje **Information Hiding** — jeden z pilířů OOP

</details>

---

### Cvičení 5.1 — Napiš nový use case

Napiš `GetRandomApodUseCase` který použije nový `getRandomApod()` endpoint z cvičení 4.1.

```kotlin
interface GetRandomApodUseCase : // ?

internal class GetRandomApodUseCaseImpl(
    private val repository: ApodRepository,
) : GetRandomApodUseCase {
    override suspend fun invoke(): // ?
}
```

---

## 6. Presentation vrstva — MVI

### Co je MVI?

**Model-View-Intent** je jednosměrný datový tok:

```
UI ──[Intent]──→ ViewModel ──[State]──→ UI
                     ↓
                  [Event]
                     ↓
              jednorázové akce (navigace, toast)
```

| Komponenta | Typ | Popis |
|------------|-----|-------|
| `State` | `data class` | Snapshot toho, co se zobrazuje. Immutable. |
| `Intent` | `sealed interface` | Uživatelská akce nebo systémový trigger |
| `Event` | `sealed interface` | Jednorázová akce (navigace, dialog, snackbar) |

### Reálný příklad — ApodViewModel

```kotlin
// shared/apod/.../presentation/ApodViewModel.kt

class ApodViewModel(
    private val getTodayApod: GetTodayApodUseCase,
    private val getApodForDate: GetApodForDateUseCase,
) : BaseScopedViewModel<ApodState, ApodIntent, ApodEvent>() {

    // Privátní mutable state — Molecule framework
    private var apod: Apod? by mutableStateOf(null)
    private var isLoading: Boolean by mutableStateOf(false)
    private var error: ErrorResult? by mutableStateOf(null)

    // Veřejný State — sestavený z private properties
    @Composable
    override fun getState() = ApodState(
        apod = apod,
        isLoading = isLoading,
        error = error,
    )

    // Vstupní bod pro Intent
    override fun onIntent(intent: ApodIntent) {
        when (intent) {
            is ApodIntent.LoadToday   -> loadToday()
            is ApodIntent.LoadForDate -> loadForDate(intent.date)
            is ApodIntent.Share       -> viewModelScope.launch {
                apod?.let { _events.emit(ApodEvent.Share(it)) }
            }
            is ApodIntent.DismissError -> error = null
        }
    }

    private fun loadToday() {
        viewModelScope.launch {
            isLoading = true
            error = null
            getTodayApod()
                .alsoOnSuccess { apod = it; isLoading = false }
                .alsoOnError  { error = it; isLoading = false }
        }
    }
}
```

### State — immutable snapshot

```kotlin
data class ApodState(
    val apod: Apod? = null,           // null = ještě se nenačetlo
    val isLoading: Boolean = false,
    val error: ErrorResult? = null,
    val selectedDate: String? = null,
) : VmState
```

### Intent — uživatelské akce

```kotlin
sealed interface ApodIntent : VmIntent {
    data object LoadToday : ApodIntent               // bez dat
    data class LoadForDate(val date: String) : ApodIntent  // s daty
    data class OpenFullscreen(val apod: Apod) : ApodIntent
    data object Share : ApodIntent
    data object DismissError : ApodIntent
}
```

### Event — jednorázové akce

```kotlin
sealed interface ApodEvent : VmEvent {
    data class OpenFullscreen(val apod: Apod) : ApodEvent
    data class Share(val apod: Apod) : ApodEvent
}
```

> **Proč Events?** Navigace a dialogy nesmí být v State — pokud uložíš "naviguj na detail" do State,
> po rotaci displeje nebo obnovení UI se navigace znovu spustí. Event se spotřebuje jednou a zmizí.

---

### Kvíz 6.1

**Otázka:** Uživatel klikne na tlačítko "Sdílet". Co přesně se stane krok po kroku,
od kliknutí po zobrazení iOS share sheetu?

<details>
<summary>Odpověď</summary>

1. SwiftUI view zavolá `viewModel.onIntent(ApodIntent.Share)`
2. ViewModel zpracuje intent v `onIntent` — zavolá `_events.emit(ApodEvent.Share(apod))`
3. Kotlin `SharedFlow<ApodEvent>` vyemituje event
4. SKIE ho zpřístupní jako `AsyncSequence` v Swiftu
5. iOS view v `.bindViewModel(viewModel, onEvent: onEvent)` zachytí event
6. Switch ve funkci `onEvent` rozpozná `.share` case
7. iOS kód otevře `UIActivityViewController`

</details>

---

### Kvíz 6.2

**Otázka:** Proč jsou `State`, `Intent` a `Event` `sealed`? Co by se stalo, kdybychom použili open class?

<details>
<summary>Odpověď</summary>

- `sealed` = kompilátor ví o **všech** podtypech v době kompilace
- `when` na sealed class je **exhaustive** — Kotlin tě donutí ošetřit každý případ
- S `open class` by `when` mohl mít nezachycené případy (runtime crash nebo silent bug)
- Sealed třídy jsou ideální pro stavové automaty (MVI je přesně to)

</details>

---

### Cvičení 6.1 — Přidej Intent pro refresh

Do `ApodIntent` přidej intent `Refresh` a zpracuj ho ve ViewModelu tak, aby:
1. Ignoroval cache a vždy zavolal API
2. Zobrazil loading indicator

```kotlin
// Hint: potřebuješ upravit Repository nebo UseCase tak,
// aby šlo přeskočit cache
```

---

## 7. Dependency Injection s Koin

### Proč Koin?

Koin je lightweight DI framework napsaný čistě v Kotlinu — funguje na všech KMP platformách.
Žádný reflection, žádný annotation processing.

### Koin modul feature

```kotlin
// shared/apod/.../di/ApodModule.kt

val apodModule = module {
    // Service dostane pojmenovaný HttpClient pro APOD API
    single { ApodService(get<HttpClient>(named(NasaClientNames.APOD))) }

    // singleOf = zkrácený zápis pro třídy s DI constructorem
    singleOf(::ApodCacheSource)

    // bind = říká Koin: "ApodRepositoryImpl implementuje ApodRepository"
    singleOf(::ApodRepositoryImpl) bind ApodRepository::class

    singleOf(::GetTodayApodUseCaseImpl) bind GetTodayApodUseCase::class
    singleOf(::GetApodForDateUseCaseImpl) bind GetApodForDateUseCase::class

    // viewModelOf = speciální scope pro ViewModely
    viewModelOf(::ApodViewModel)
}
```

### Scopy v Koin

| Funkce | Lifecycle | Použití |
|--------|-----------|---------|
| `single { }` | Singleton — žije celý čas appky | Service, Repository, UseCase |
| `factory { }` | Nová instance při každém `get()` | Jednorázové objekty |
| `viewModelOf { }` | Scope = lifecycle ViewModelu | Všechny ViewModely |

### Umbrella modul — agregace

```kotlin
// shared/umbrella — startovací bod pro iOS

fun initKoinIos(
    doOnStartup: () -> Unit,
    config: () -> Config,
): KoinApplication = startKoin {
    modules(
        baseModule,
        nasaDatabaseModule,
        networkModule,
        authModule,
        apodModule,
        galleryModule,
        searchModule,
        favoritesModule,
    )
}
```

### Jak iOS dostane ViewModel z Koin

```swift
// ios/Application/DependencyInjection/Sources/DependencyInjection/KMPViewModels.swift

public extension Container {
    // Koin singleton přes Factory (Swift DI framework)
    private var kmp: Factory<KMPDependency> { self { KMPKoinDependency() }.singleton }

    // Každý ViewModel má svůj Factory accessor
    var apodViewModel: Factory<ApodViewModel> {
        self { self.kmp().get(ApodViewModel.self) }
    }
}

// V SwiftUI View:
@Injected(\.apodViewModel) private var viewModel: ApodViewModel
```

---

### Cvičení 7.1 — Zaregistruj nový use case

Přidej `GetRandomApodUseCase` z cvičení 5.1 do `ApodModule`:

```kotlin
val apodModule = module {
    // ... existující registrace ...

    // přidej sem:
}
```

---

## 8. SKIE — Swift/Kotlin interop

### Problém bez SKIE

Kotlin generics, sealed classes, coroutines a Flow nejsou přirozeně kompatibilní se Swift/Objective-C.
Bez SKIE by `Result<Apod>` byl v Swiftu jen `KotlinAny` a `Flow<State>` by neexistoval jako `AsyncSequence`.

### Co SKIE generuje automaticky

| Kotlin | Swift (díky SKIE) |
|--------|-------------------|
| `sealed interface ApodIntent` | `enum ApodIntent` s associated values |
| `sealed class ApodEvent` | `enum ApodEvent` s associated values |
| `StateFlow<ApodState>` | `AsyncSequence` (použitelný v `for await`) |
| `SharedFlow<ApodEvent>` | `AsyncSequence` |
| `suspend fun` | `async throws func` |

### Práce se sealed class v Swiftu

```swift
// Kotlin sealed interface se v Swiftu stane enum-like strukturou
// Použij onEnum() helper pro exhaustive switch

private func onEvent(_ event: ApodEvent) {
    switch onEnum(of: event) {
    case .openFullscreen(let data):
        // data.apod je silně typovaný Apod objekt
        fullscreenApod = data.apod
    case .share(let data):
        shareApod = data.apod
    }
}
```

### Přístup ke singleton objektům

```swift
// Kotlin companion object nebo object
// SKIE naming: TypeName.shared (pro object) nebo TypeName(params:) pro class

// Kotlin:
object NasaApiConstants { val APOD = "..." }

// Swift (díky SKIE):
NasaApiConstants.shared.APOD
```

### Pozor na iOS 16 navigaci

```swift
// SKIE generuje @available(iOS 17, *) pro některé async API
// Pokud cílíš iOS 16, použij task { } wrapper:

.task {
    for await state in viewModel.state {
        self.state = state
    }
}
```

---

## 9. iOS — Pattern B

### Pravidla Pattern B

1. **Jeden soubor** = jeden feature view (`ApodView.swift`, ne `ApodScreen.swift`)
2. Obsahuje VM bridge + kompletní UI
3. `body` je čistá kostra — volá pouze `private var`
4. Každý vizuální blok = vlastní `private var` nebo `private struct`

### Anatomy iOS Feature View

```swift
// ios/PresentationLayer/ApodFeature/Sources/ApodFeature/ApodView.swift

import DependencyInjection
import Factory
import KMPShared
import SwiftUI
import UIToolkit

public struct ApodView: View {

    // 1. VM bridge — Koin přes Factory
    @Injected(\.apodViewModel) private var viewModel: ApodViewModel

    // 2. Lokální UI state (odvozený z KMP state)
    @State private var state = ApodState()
    @State private var showFullscreen = false

    public init() {}

    // 3. body = POUZE kostra, žádná inline logika
    public var body: some View {
        ZStack {
            if state.isLoading { loadingView }
            else if let error = state.error { errorView(error) }
            else { contentView }
        }
        // 4. Napojení na KMP ViewModel
        .task { for await s in viewModel.state { state = s } }
        .bindViewModel(viewModel, onEvent: onEvent)
        .onAppear { viewModel.onViewAppeared() }
    }

    // 5. Každý blok = private var
    private var loadingView: some View {
        NasaLoadingView()
    }

    private var contentView: some View {
        ScrollView {
            VStack(spacing: .spaceMD) {
                heroSection
                descriptionSection
            }
        }
    }

    private var heroSection: some View {
        // pouze design system tokeny — nikdy hardcoded čísla/barvy
        ApodHeroCard(apod: state.apod)
            .padding(.horizontal, .spaceMD)
    }

    private var descriptionSection: some View {
        if let apod = state.apod {
            Text(apod.explanation)
                .font(.nasaBody)
                .foregroundColor(.nasaOnSurface)
                .padding(.horizontal, .spaceMD)
        }
    }

    // 6. Event handling
    private func onEvent(_ event: ApodEvent) {
        switch onEnum(of: event) {
        case .openFullscreen(let data):
            showFullscreen = true
        case .share(let data):
            // iOS share sheet
            break
        }
    }

    // Vnořené komponenty specifické pro tento screen
    // MARK: -
    private struct ErrorView: View {
        let error: ErrorResult
        let onRetry: () -> Void

        var body: some View {
            NasaErrorView(message: error.localizedMessage.localized(), onRetry: onRetry)
        }
    }

    private func errorView(_ error: ErrorResult) -> some View {
        ErrorView(error: error) {
            viewModel.onIntent(ApodIntent.LoadToday())
        }
    }
}
```

### Design systém tokeny — povinné použití

```swift
// NIKDY toto:
.padding(16)
.foregroundColor(Color(hex: "#0B3D91"))
.font(.system(size: 17, weight: .semibold))

// VŽDY toto:
.padding(.spaceMD)
.foregroundColor(.nasaPrimary)
.font(.nasaTitle)
```

| Token | Typ | Hodnota |
|-------|-----|---------|
| `.spaceXS` | `CGFloat` | 4 |
| `.spaceSM` | `CGFloat` | 8 |
| `.spaceMD` | `CGFloat` | 16 |
| `.spaceLG` | `CGFloat` | 24 |
| `.spaceXL` | `CGFloat` | 32 |
| `.nasaPrimary` | `Color` | NASA modrá |
| `.nasaBackground` | `Color` | Pozadí |
| `.nasaBody` | `Font` | Body text |
| `.nasaHeadline` | `Font` | Nadpisy |

---

### Kvíz 9.1

**Otázka:** Proč nesmí `body` obsahovat inline logiku jako `if state.isLoading { ... } else { ... }`
přímo mezi view buildery?

<details>
<summary>Odpověď</summary>

- Čitelnost: `body` by se stal nepřehledný při více podmínkách
- Testovatelnost: `private var loadingView` lze izolovat a vizuálně testovat v Preview
- Separace concerns: každý blok má jasnou zodpovědnost a jméno
- SwiftUI ViewBuilder má limity — hluboce vnořené podmínky mohou způsobit pomalou kompilaci

</details>

---

### Cvičení 9.1 — Rozlož view na private vars

Vezmi tento "špatný" kód a rozlož ho podle Pattern B:

```swift
// Špatně — vše inline v body:
var body: some View {
    VStack {
        if state.isLoading {
            ProgressView()
                .tint(.nasaPrimary)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else {
            HStack {
                AsyncImage(url: URL(string: state.apod?.url ?? ""))
                    .frame(width: 80, height: 80)
                    .clipShape(RoundedRectangle(cornerRadius: .radiusMD))
                VStack(alignment: .leading) {
                    Text(state.apod?.title ?? "")
                        .font(.nasaTitle)
                    Text(state.apod?.date ?? "")
                        .font(.nasaCaption)
                        .foregroundColor(.nasaSubtle)
                }
            }
            .padding(.spaceMD)
        }
    }
}
```

<details>
<summary>Řešení</summary>

```swift
var body: some View {
    if state.isLoading { loadingView } else { contentView }
}

private var loadingView: some View {
    NasaLoadingView()
}

private var contentView: some View {
    apodRow
        .padding(.spaceMD)
}

private var apodRow: some View {
    HStack {
        NasaAsyncImage(url: state.apod?.url)
            .frame(width: 80, height: 80)
            .clipShape(RoundedRectangle(cornerRadius: .radiusMD))
        apodInfo
    }
}

private var apodInfo: some View {
    VStack(alignment: .leading) {
        Text(state.apod?.title ?? "")
            .font(.nasaTitle)
        Text(state.apod?.date ?? "")
            .font(.nasaCaption)
            .foregroundColor(.nasaSubtle)
    }
}
```

</details>

---

## 10. SQLDelight

### Co je SQLDelight?

SQLDelight generuje typově bezpečné Kotlin API ze SQL souborů. Definuješ tabulky a dotazy v `.sq` souborech, SQLDelight generuje Kotlin kód.

### Příklad — ApodCache.sq

```sql
-- shared/database/src/commonMain/sqldelight/cz/tomasbrand/nasagallery/database/ApodCache.sq

CREATE TABLE ApodCache (
    date TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    explanation TEXT NOT NULL,
    url TEXT NOT NULL,
    hdUrl TEXT,
    mediaType TEXT NOT NULL,
    copyright TEXT,
    thumbnailUrl TEXT
);

-- Pojmenovaný dotaz → generuje funkci apodCacheQueries.getByDate(date)
getByDate:
SELECT * FROM ApodCache WHERE date = ?;

-- Insert or replace
upsert:
INSERT OR REPLACE INTO ApodCache VALUES (?, ?, ?, ?, ?, ?, ?, ?);
```

### Koin registrace DB

```kotlin
// shared/database/.../di/NasaDatabaseModule.kt

val nasaDatabaseModule = module {
    singleOf(::DatabaseDriverFactory)

    single {
        NasaDatabase(driver = get<DatabaseDriverFactory>().createDriver())
    }

    // Exponuj queries jako singleton — každý modul si vezme co potřebuje
    single { get<NasaDatabase>().favoriteQueries }
    single { get<NasaDatabase>().apodCacheQueries }
}
```

### Platform-specific driver

```kotlin
// iosMain — iOS SQLite driver
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(NasaDatabase.Schema, "nasa.db")
}

// androidMain — Android SQLite driver
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(NasaDatabase.Schema, context, "nasa.db")
}
```

---

## 11. Jak přidat nový feature modul

Postup přidání nového feature "weather" (krok po kroku):

### Krok 1: Vytvoř Gradle modul

```
shared/weather/
  src/
    commonMain/kotlin/cz/tomasbrand/nasagallery/weather/
      data/
        remote/  WeatherService.kt
        dto/     WeatherDto.kt
      domain/
        model/   Weather.kt
        usecase/ GetWeatherUseCase.kt
      presentation/
        WeatherViewModel.kt
        WeatherState.kt
        WeatherIntent.kt
        WeatherEvent.kt
      di/
        WeatherModule.kt
```

### Krok 2: Definuj domain model

```kotlin
// domain/model/Weather.kt
data class Weather(
    val temperature: Double,
    val description: String,
)
```

### Krok 3: DTO

```kotlin
// data/dto/WeatherDto.kt
@Serializable
data class WeatherDto(
    val temp: Double,
    @SerialName("weather_desc") val weatherDesc: String,
)
```

### Krok 4: Service

```kotlin
internal class WeatherService(private val client: HttpClient) {
    suspend fun getWeather(): Result<WeatherDto> =
        runCatchingCommonNetworkExceptions {
            client.get("https://api.example.com/weather").body()
        }
}
```

### Krok 5: Repository

```kotlin
internal interface WeatherRepository {
    suspend fun get(): Result<Weather>
}

internal class WeatherRepositoryImpl(
    private val service: WeatherService,
) : WeatherRepository {
    override suspend fun get(): Result<Weather> =
        service.getWeather().map { it.toWeather() }
}

private fun WeatherDto.toWeather() = Weather(
    temperature = temp,
    description = weatherDesc,
)
```

### Krok 6: UseCase

```kotlin
interface GetWeatherUseCase : UseCaseResultNoParams<Weather>

internal class GetWeatherUseCaseImpl(
    private val repository: WeatherRepository,
) : GetWeatherUseCase {
    override suspend fun invoke(): Result<Weather> = repository.get()
}
```

### Krok 7: MVI (State + Intent + Event + ViewModel)

```kotlin
data class WeatherState(
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    val error: ErrorResult? = null,
) : VmState

sealed interface WeatherIntent : VmIntent {
    data object Load : WeatherIntent
    data object Refresh : WeatherIntent
}

sealed interface WeatherEvent : VmEvent {
    data object ShowError : WeatherEvent
}

class WeatherViewModel(
    private val getWeather: GetWeatherUseCase,
) : BaseScopedViewModel<WeatherState, WeatherIntent, WeatherEvent>() {

    private var weather: Weather? by mutableStateOf(null)
    private var isLoading: Boolean by mutableStateOf(false)
    private var error: ErrorResult? by mutableStateOf(null)

    @Composable
    override fun getState() = WeatherState(weather, isLoading, error)

    override fun onViewAppeared() { load() }

    override fun onIntent(intent: WeatherIntent) {
        when (intent) {
            is WeatherIntent.Load    -> load()
            is WeatherIntent.Refresh -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            isLoading = true
            getWeather()
                .alsoOnSuccess { weather = it; isLoading = false }
                .alsoOnError  { error = it;   isLoading = false }
        }
    }
}
```

### Krok 8: Koin modul

```kotlin
val weatherModule = module {
    singleOf(::WeatherService)
    singleOf(::WeatherRepositoryImpl) bind WeatherRepository::class
    singleOf(::GetWeatherUseCaseImpl) bind GetWeatherUseCase::class
    viewModelOf(::WeatherViewModel)
}
```

### Krok 9: Přidej do umbrella

```kotlin
// shared/umbrella/.../KoinIOS.kt
fun initKoinIos(...): KoinApplication = startKoin {
    modules(
        // ... ostatní moduly ...
        weatherModule, // ← přidej sem
    )
}
```

### Krok 10: iOS Factory + View

```swift
// KMPViewModels.swift
var weatherViewModel: Factory<WeatherViewModel> {
    self { self.kmp().get(WeatherViewModel.self) }
}

// WeatherView.swift
public struct WeatherView: View {
    @Injected(\.weatherViewModel) private var viewModel: WeatherViewModel
    @State private var state = WeatherState()

    public var body: some View {
        contentView
            .task { for await s in viewModel.state { state = s } }
            .bindViewModel(viewModel, onEvent: onEvent)
            .onAppear { viewModel.onViewAppeared() }
    }

    private var contentView: some View {
        // ... UI ...
    }

    private func onEvent(_ event: WeatherEvent) {
        switch onEnum(of: event) {
        case .showError:
            break
        }
    }
}
```

---

## 12. Kvízy a cvičení

### Finální kvíz — Zkontroluj si znalosti

**Q1:** Jaký je rozdíl mezi `single { }` a `factory { }` v Koin?

**Q2:** Proč `ApodRepositoryImpl` vrací `Result<Apod>` a ne `Apod`?

**Q3:** Kdo volá `viewModel.onViewAppeared()` na iOS?

**Q4:** V jakém souboru a jak zaregistruješ nový ViewModel do iOS Factory?

**Q5:** Proč nesmí domain model (`Apod.kt`) importovat Ktor nebo SQLDelight?

**Q6:** Co je `@Composable` anotace na `getState()` ve ViewModelu a proč tam je?

<details>
<summary>Odpovědi</summary>

**A1:** `single` = singleton — stejná instance po celou dobu běhu. `factory` = nová instance při každém `get()`.

**A2:** Síťové operace mohou selhat. `Result<T>` nutí volajícího ošetřit chybu — nikdy nemůže "zapomenout" na error stav. `throw` by vedl k `try/catch` peklu.

**A3:** Přímo v SwiftUI View: `.onAppear { viewModel.onViewAppeared() }`

**A4:** `ios/Application/DependencyInjection/Sources/DependencyInjection/KMPViewModels.swift` — přidáš `var myViewModel: Factory<MyViewModel> { self { self.kmp().get(MyViewModel.self) } }`

**A5:** Domain vrstva musí být nezávislá na frameworcích. Kdybychom změnili Ktor za jiný HTTP klient, domain model by se neměl vůbec měnit. Toto je jádro Clean Architecture.

**A6:** Molecule framework — `getState()` je Composable funkce která automaticky reaktivně sleduje `mutableStateOf` properties a generuje nový State vždy když se změní. Díky tomu StateFlow funguje bez ručního `emit`.

</details>

---

### Komplexní cvičení — Weather feature

Postup:
1. Vytvoř `WeatherService` volající `https://api.open-meteo.com/v1/forecast?latitude=50.08&longitude=14.44&current_weather=true`
2. Definuj `WeatherDto` odpovídající JSON odpovědi
3. Napiš `WeatherRepository` s cache (ulož poslední teplotu do UserDefaults)
4. Napiš `GetCurrentWeatherUseCase`
5. Vytvoř `WeatherViewModel` s MVI
6. Zaregistruj v Koin
7. Přidej `WeatherView` na iOS s Pattern B (loading, error, content states)

---

### Cheat sheet — Rychlý přehled

```
Přidávám novou feature:
  1. data/remote/    → XyzService.kt (HTTP)
  2. data/dto/       → XyzDto.kt (@Serializable)
  3. data/local/     → XyzSource.kt (SQLDelight, volitelné)
  4. data/repository → XyzRepositoryImpl.kt (cache → network, mapping)
  5. domain/model/   → Xyz.kt (čistý model)
  6. domain/usecase/ → GetXyzUseCase.kt (interface + impl)
  7. presentation/   → XyzViewModel.kt + XyzState/Intent/Event
  8. di/             → XyzModule.kt (Koin registrace)
  9. umbrella        → přidej XyzModule do initKoin
 10. ios/DI          → přidej Factory accessor
 11. ios/Presentation → XyzView.swift (Pattern B)

Pravidla:
  ✓ Každá vrstva zná jen tu níže přes interface
  ✓ Result<T> všude v domain a data
  ✓ Nikdy nevrhej exception mimo Data vrstvu
  ✓ State je immutable data class
  ✓ Events jsou jednorázové (navigace, dialogy)
  ✓ body na iOS = čistá kostra, jen private var
  ✓ Vždy .nasaXX tokeny, nikdy hardcoded hodnoty
```

---

> **Autor:** Dokumentace vygenerována pro projekt NASA Gallery — KMP (Kotlin 2.2.10 + SKIE 0.10.6 + SwiftUI)
> Aktualizováno: 2026-04-09
