<p align="center">
  <img src="./Assets/svg/logo_app_icon.svg" width="120" alt="Sombra logo" />
</p>

<h1 align="center">Sombra</h1>

<p align="center">
  An Android app that helps you stay safe in the sun.<br />
  Real-time UV index · personalised safe exposure time · sunscreen reminders · home screen widget
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.2-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin 2.2" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/minSdk-24-3DDC84?logo=android&logoColor=white" alt="minSdk 24" />
  <img src="https://img.shields.io/badge/targetSdk-36-3DDC84?logo=android&logoColor=white" alt="targetSdk 36" />
  <img src="https://img.shields.io/badge/Ktor-3.1-087CFA?logo=ktor&logoColor=white" alt="Ktor 3.1" />
  <img src="https://img.shields.io/badge/Hilt-DI-F07F14" alt="Hilt" />
  <img src="https://img.shields.io/badge/status-in%20development-F07F14" alt="Status: in development" />
</p>

<p align="center">
  <a href="#why">Why</a> ·
  <a href="#screenshots">Screenshots</a> ·
  <a href="#features">Features</a> ·
  <a href="#current-status">Status</a> ·
  <a href="#architecture">Architecture</a> ·
  <a href="#getting-started">Getting started</a> ·
  <a href="#roadmap">Roadmap</a>
</p>

---

## Why

Most people check the temperature before going out, but temperature isn't what burns you. **UV radiation is.** A cool, cloudy day can still have a UV index of 6 or 7, because clouds let most UV through.

Sombra puts the **UV index first**. It tells you how long you can stay outside before your skin starts to burn, based on your skin type, and reminds you when it's time to reapply sunscreen.

## Screenshots

<table align="center">
  <tr>
    <td align="center"><img src="./Assets/mock/splash_screen.png" width="200" alt="Splash screen" /></td>
    <td align="center"><img src="./Assets/mock/home_screen.png" width="200" alt="Home, sunny" /></td>
    <td align="center"><img src="./Assets/mock/home_cloud_screen.png" width="200" alt="Home, cloudy" /></td>
  </tr>
  <tr>
    <td align="center"><b>Splash</b></td>
    <td align="center"><b>Home · sunny</b></td>
    <td align="center"><b>Home · cloudy</b></td>
  </tr>
  <tr>
    <td align="center"><img src="./Assets/mock/prevision_screen.png" width="200" alt="UV forecast" /></td>
    <td align="center"><img src="./Assets/mock/sun_timer_screen.png" width="200" alt="Sunscreen timer" /></td>
    <td align="center"><img src="./Assets/mock/profile_screen.png" width="200" alt="Profile" /></td>
  </tr>
  <tr>
    <td align="center"><b>UV forecast</b></td>
    <td align="center"><b>Sunscreen timer</b></td>
    <td align="center"><b>Profile</b></td>
  </tr>
</table>

> [!NOTE]
> These are design mockups. They will be replaced with real screenshots as each screen is implemented.

## Features

| | Feature | Description |
|:---:|---|---|
| ☀️ | **Real-time UV index** | For your current location, colour-coded with the WHO UV scale |
| ⏱️ | **Safe exposure time** | "You'll burn in ~20 min without sunscreen", based on your skin type (Fitzpatrick I–VI) and the current UV |
| 🧴 | **SPF recommendation** | Adapts to the UV level and your skin |
| 🔔 | **Sunscreen reminders** | Reapply every 2 hours, or every 40 minutes after swimming |
| ⚠️ | **Daily UV alerts** | "UV will be high today at 1 PM" |
| 📅 | **Forecast** | Hourly forecast with safe time windows (UV below 3) and a 5-day outlook |
| 📱 | **Home screen widget** | Current UV and time until the next reapplication |
| 📴 | **Works offline** | The last forecast is cached and shown with its update time |
| 🏅 | **Levels and patches** | Rewards protective habits (reapplying on time, avoiding peak hours), never time spent in the sun |
| 🌗 | **Themes and languages** | Light and dark theme, English and Portuguese |

## Current status

Every screen's UI is built in Compose and connected through navigation. The Open-Meteo client is built and unit-tested, but it isn't connected to the screens yet. Most screens still show sample data from their `UiState`.

| Screen | Package | UI | Real data |
|---|---|:---:|:---:|
| Splash | `app` · `main/` | ✅ | — |
| Login | `presentation/login` | ✅ | 🔜 Supabase |
| Register (with password strength) | `presentation/register` | ✅ | 🔜 Supabase |
| Skin type onboarding (Fitzpatrick grid, sun-place chips, step progress) | `presentation/skintype` | ✅ | 🔜 DataStore |
| Home (loading skeleton, error and retry, offline banner, timer card) | `presentation/home` | ✅ | 🟡 Timer is live, weather is still sample data |
| UV forecast (hourly bar chart, 5-day cards) | `presentation/forecast` | ✅ | 🔜 Open-Meteo |
| Sunscreen timer (ring, SPF selector, water toggle) | `presentation/timer` | ✅ | 🔜 |
| Profile (avatar, stats, level progress, patches) | `presentation/profile` | ✅ | 🔜 |

### Navigation flow

```mermaid
flowchart LR
    S[Splash] --> L[Login] --> R[Register] --> ST[Skin type] --> H

    subgraph tabs["Bottom navigation bar"]
        H[Today] --- F[Forecast] --- T[Sunscreen] --- P[Profile]
    end
```

The bottom bar (`SombraNavigationBar`) lives in `SombraNavHost` and only appears on the four top-level tabs defined in `TopLevelDestination`. Switching tabs pops back to the start destination and saves and restores each tab's state, so tabs never pile up on the back stack.

### Levels

Users level up by building protective habits.

<table align="center">
  <tr>
    <td align="center"><img src="./Assets/svg/nivel_1_sombrinha.svg" width="64" alt="Level 1" /></td>
    <td align="center"><img src="./Assets/svg/nivel_2_filtro.svg" width="64" alt="Level 2" /></td>
    <td align="center"><img src="./Assets/svg/nivel_3_chapeu.svg" width="64" alt="Level 3" /></td>
    <td align="center"><img src="./Assets/svg/nivel_4_guarda_sol.svg" width="64" alt="Level 4" /></td>
    <td align="center"><img src="./Assets/svg/nivel_5_eclipse.svg" width="64" alt="Level 5" /></td>
  </tr>
  <tr>
    <td align="center">1 · Sombrinha</td>
    <td align="center">2 · Filtro</td>
    <td align="center">3 · Chapéu</td>
    <td align="center">4 · Guarda-sol</td>
    <td align="center">5 · Eclipse</td>
  </tr>
</table>

### Patches

<table align="center">
  <tr>
    <td align="center"><img src="./Assets/svg/patch_primeiro_protetor.svg" width="56" alt="Primeiro Protetor" /></td>
    <td align="center"><img src="./Assets/svg/patch_semana_spf.svg" width="56" alt="Semana SPF" /></td>
    <td align="center"><img src="./Assets/svg/patch_reaplicador.svg" width="56" alt="Reaplicador" /></td>
    <td align="center"><img src="./Assets/svg/patch_hora_da_sombra.svg" width="56" alt="Hora da Sombra" /></td>
  </tr>
  <tr>
    <td align="center">Primeiro Protetor</td>
    <td align="center">Semana SPF</td>
    <td align="center">Reaplicador</td>
    <td align="center">Hora da Sombra</td>
  </tr>
  <tr>
    <td align="center"><img src="./Assets/svg/patch_chapeu_no_topo.svg" width="56" alt="Chapéu no Topo" /></td>
    <td align="center"><img src="./Assets/svg/patch_uv_extremo.svg" width="56" alt="UV Extremo" /></td>
    <td align="center"><img src="./Assets/svg/patch_verao_limpo.svg" width="56" alt="Verão Limpo" /></td>
    <td align="center"><img src="./Assets/svg/patch_familia.svg" width="56" alt="Família" /></td>
  </tr>
  <tr>
    <td align="center">Chapéu no Topo</td>
    <td align="center">UV Extremo</td>
    <td align="center">Verão Limpo</td>
    <td align="center">Família</td>
  </tr>
</table>

## Tech stack

| Area | Technology | Status |
|---|---|:---:|
| Language | Kotlin 2.2, Coroutines, Flow | ✅ |
| UI | Jetpack Compose, Material 3 | ✅ |
| Navigation | Navigation Compose (type-safe routes) | ✅ |
| Splash | SplashScreen API with an animated exit | ✅ |
| Dependency injection | Hilt (KSP) | ✅ |
| Architecture | MVVM + Clean Architecture, multi-module, unidirectional data flow | ✅ |
| Networking | Ktor 3.1 (OkHttp engine, content negotiation, timeouts, logging) + kotlinx.serialization | ✅ |
| Weather data | [Open-Meteo Forecast API](https://open-meteo.com/) (no API key needed): client and DTO → domain mapper | ✅ |
| Time | `java.time` with an injected `Clock`, so time-based logic can be tested | ✅ |
| Authentication | [Supabase Auth](https://supabase.com/docs/guides/auth) via `supabase-kt`, Credential Manager | 🔜 |
| Local database | Room (forecast cache, sunscreen log, exposure history) | 🔜 |
| Preferences | DataStore (skin type, settings) | 🔜 |
| Location | Fused Location Provider (coarse location only) | 🔜 |
| Background work | WorkManager | 🔜 |
| Widget | Jetpack Glance | 🔜 |
| Unit testing | JUnit 4, Ktor `MockEngine`, `kotlinx-coroutines-test` | ✅ |
| More testing | MockK, Turbine, Compose UI tests | 🔜 |
| CI | GitHub Actions (build, lint, unit tests) | 🔜 |

✅ in place · 🟡 partly in place · 🔜 planned

## Architecture

The project is split into Gradle modules. Dependencies only point one way, so the business rules in `domain` never depend on the UI or on where the data comes from.

```mermaid
flowchart TD
    app["<b>app</b><br/>MainActivity · Application · NavHost"]
    presentation["<b>presentation</b><br/>Screens · ViewModels"]
    data["<b>data</b><br/>Repositories · Open-Meteo API · Mappers"]
    domain["<b>domain</b><br/>Models · Repository interfaces · Use cases"]
    core["<b>core</b><br/>Theme · Components · Routes"]

    app --> presentation
    app --> data
    app --> core
    presentation --> domain
    presentation --> core
    data --> domain
    data --> core
```

| Module | Responsibility |
|---|---|
| `app` | Entry point: `MainActivity`, `SombraApplication` (`@HiltAndroidApp`), splash screen and the `SombraNavHost` |
| `presentation` | One package per screen with `Screen`, `ViewModel`, `UiState`, its navigation entry and a `component/` folder |
| `domain` | Models, repository interfaces and use cases, split by feature (`weather`, `sunscreen`). Depends on no other module |
| `data` | Hilt modules, Ktor client, Open-Meteo API, DTO → domain mappers and repository implementations |
| `core` | Design system (`SombraTheme`, `SombraColors`, type, shapes), shared components and the navigation routes |

### Data flow

```mermaid
flowchart LR
    UI["Compose screens"] --> VM["ViewModels"]
    VM --> UC["Use cases"]
    UC --> R["Repositories"]
    R --> API["Open-Meteo API<br/>(Ktor)"]
    R --> DB[("Room")]
    R --> DS[("DataStore")]
    R --> SB["Supabase Auth"]
    W["WorkManager workers"] --> R
    G["Glance widget"] --> DB
```

The target design is **offline-first**: screens always read from Room. Repositories refresh the cache from the network in the background and expose the result as a `Flow`. The widget reads only from Room and never makes network calls.

## Project structure

```
Sombra/
├── Assets/                         # Logo, mockups, illustrations, nav icons (SVG, PNG, Android drawables)
├── README.md
└── Sombra/                         # Android Studio project
    ├── app/
    │   └── com/dmm/sombra/
    │       ├── MainActivity.kt         # Splash screen + setContent
    │       ├── SombraApplication.kt    # @HiltAndroidApp
    │       └── navigation/
    │           └── SombraNavHost.kt    # NavHost + shared bottom navigation bar
    ├── presentation/
    │   └── com/dmm/presentation/
    │       ├── main/                   # MainViewModel (session check behind the splash)
    │       ├── login/                  # Sky header, "or" divider
    │       ├── register/               # Password strength label
    │       ├── skintype/               # Skin type grid and cards, sun-place chips, onboarding footer
    │       ├── home/                   # Timer card, skeleton, offline banner, weather details
    │       │   ├── HomeScreen.kt       # HomeScreen (stateful) + HomeContent (stateless)
    │       │   ├── HomeViewModel.kt
    │       │   ├── HomeUiState.kt      # Loading · Error · Content
    │       │   ├── HomeNavigation.kt   # NavGraphBuilder.homeScreen()
    │       │   └── TimerCardMapper.kt  # TimerStatus → TimerCardUiState
    │       ├── forecast/               # Bar chart, legend, hourly UV card, UV day cards
    │       ├── timer/                  # Timer ring, SPF selector, water toggle, stats card, buttons
    │       └── profile/                # Avatar, header, stat cards, level progress, patches
    ├── domain/
    │   └── com/dmm/domain/
    │       ├── weather/model/          # Forecast, CurrentWeather, HourlyUv, DailyForecast, UvLevel, WeatherCondition
    │       └── sunscreen/
    │           ├── model/              # SunscreenSession, TimerStatus
    │           ├── repository/         # SunscreenRepository (interface)
    │           └── usecase/            # ObserveTimerStatusUseCase
    ├── data/
    │   └── com/dmm/data/
    │       ├── di/                     # DataModule (bindings, Clock), NetworkModule (HttpClient)
    │       ├── remote/                 # OpenMeteoApi + dto/ForecastResponseDto
    │       ├── mapper/                 # ForecastMapper (DTO → domain)
    │       └── repository/             # InMemorySunscreenRepository
    ├── core/
    │   └── com/dmm/core/
    │       ├── designsystem/           # SombraTheme, SombraColors, Type, Shape
    │       ├── components/             # Buttons, text field, top bar, nav bar, skeleton, step progress
    │       └── navigation/             # SombraRoutes, TopLevelDestination
    └── gradle/
        └── libs.versions.toml          # All dependency versions
```

### Shared components (`core/components`)

| Component | Used for |
|---|---|
| `SombraSunButton`, `SombraDarkButton`, `SombraSecondaryButton` | Primary, dark and outlined actions |
| `SombraTextField` | Email and password inputs |
| `SombraTopBar`, `SombraNavigationBar` | App bars |
| `SombraStepProgressBar` | Onboarding progress |
| `SombraSkeleton` | Loading placeholders |
| `SombraLinkPrompt`, `SombraButtonLabel` | "Don't have an account? Sign up" style links, button text |

### Adding a new screen

1. Add a route in `core/navigation/SombraRoutes.kt`:

   ```kotlin
   @Serializable
   data object SkinTypeRoute
   ```

2. Create a package in `presentation/` with `SkinTypeScreen.kt`, `SkinTypeViewModel.kt`, `SkinTypeUiState.kt` and `SkinTypeNavigation.kt`, using `home/` as a template. Put screen-only composables in a `component/` subfolder.
3. Register it in `SombraNavHost.kt` with `skinTypeScreen(...)`, and open it with `navController.navigate(SkinTypeRoute)`.
4. If it should be a bottom-bar tab, add it to `TopLevelDestination` with an icon and a label.

## Getting started

### Requirements

- Android Studio (latest stable)
- JDK 17
- An Android device or emulator running **Android 7.0 (API 24)** or later
- A free [Supabase](https://supabase.com/) project (not needed yet: only once login is connected)

### 1. Clone the repository

```bash
git clone https://github.com/DMarques24/Sombra.git
cd Sombra/Sombra
```

### 2. Set up Supabase (planned)

> [!NOTE]
> Login isn't connected to Supabase yet, so you can skip this step for now.

1. Create a project at [supabase.com](https://supabase.com/).
2. Under **Authentication → Providers**, enable **Email**.
3. Under **Project Settings → API**, copy the **Project URL** and the **anon public key**.
4. Add them to `local.properties` in the Android project folder. This file is git-ignored, so keys are never committed:

   ```properties
   SUPABASE_URL=https://your-project-id.supabase.co
   SUPABASE_ANON_KEY=your-anon-key
   ```

   They are exposed to the app through `BuildConfig`.

### 3. Run

Open the `Sombra/` folder in Android Studio, wait for Gradle to sync, and run the `app` configuration.

From the command line:

```bash
./gradlew installDebug        # build and install on a connected device
./gradlew testDebugUnitTest   # unit tests
./gradlew :data:testDebugUnitTest  # only the data layer tests (API + mapper)
./gradlew connectedCheck      # UI tests (device or emulator needed)
./gradlew lint                # static analysis
```

Open-Meteo doesn't need an API key, so weather data works straight away.

### Tests

| Test | What it checks |
|---|---|
| `OpenMeteoApiTest` | Uses Ktor's `MockEngine` to check the URL and query parameters, JSON → DTO parsing, server errors and a missing `uv_index` |
| `ForecastMapperTest` | DTO → domain mapping using a real saved response (`forecast_coimbra.json`), including the hourly UV fallback |
| `OpenMeteoApiLiveTest` | Calls the real API for Coimbra. It's marked `@Ignore` because it needs internet, so run it by hand |

## How it works

### UV data

The app calls the Open-Meteo forecast endpoint with the device's coarse location:

```http
GET https://api.open-meteo.com/v1/forecast
    ?latitude=40.20&longitude=-8.41
    &current=temperature_2m,apparent_temperature,is_day,weather_code,uv_index
    &hourly=uv_index
    &daily=weather_code,temperature_2m_max,temperature_2m_min,uv_index_max,sunset
    &timezone=auto
    &forecast_days=5
```

`ForecastMapper` turns the response into a domain `Forecast` (current weather, hourly UV, 5 daily entries and the fetch time). If the `current` block has no `uv_index`, it uses the UV value for the current hour from the hourly list instead.

`WeatherCondition.fromWmoCode()` turns the `weather_code` into `Clear`, `PartlyCloudy`, `Cloudy`, `Fog`, `Rain`, `Snow` or `Storm`. Unknown codes fall back to `Cloudy`. The condition picks the illustration shown on the Home screen:

<table align="center">
  <tr>
    <td align="center"><img src="./Assets/svg/weather_sol.svg" width="72" alt="Clear sky" /></td>
    <td align="center"><img src="./Assets/svg/weather_sol_nublado.svg" width="72" alt="Partly cloudy" /></td>
    <td align="center"><img src="./Assets/svg/weather_nublado.svg" width="72" alt="Cloudy" /></td>
    <td align="center"><img src="./Assets/svg/weather_chuva.svg" width="72" alt="Rain" /></td>
    <td align="center"><img src="./Assets/svg/weather_noite.svg" width="72" alt="Night" /></td>
  </tr>
  <tr>
    <td align="center">Clear<br /><code>0, 1</code></td>
    <td align="center">Partly cloudy<br /><code>2</code></td>
    <td align="center">Cloudy · Fog<br /><code>3 · 45, 48</code></td>
    <td align="center">Rain · Storm<br /><code>51–67, 80–82 · 95–99</code></td>
    <td align="center">Night<br /><code>is_day = 0</code></td>
  </tr>
</table>

### UV scale (WHO)

`UvLevel.from(uvIndex)` rounds the value and maps it onto the scale:

| UV index | Level | Colour |
|:---:|---|---|
| 0–2 | Low | 🟢 Green |
| 3–5 | Moderate | 🟡 Yellow |
| 6–7 | High | 🟠 Orange |
| 8–10 | Very high | 🔴 Red |
| 11+ | Extreme | 🟣 Violet |

### Safe exposure time

Each Fitzpatrick skin type has a minimal erythemal dose (MED), the amount of UV energy that causes visible reddening. One UV index unit is 0.025 W/m² of erythemal irradiance, so:

```
minutes to burn ≈ MED (J/m²) / (UV index × 0.025 × 60)
```

For example, skin type II (MED ≈ 250 J/m²) at UV 8 gives about 20 minutes.

> [!IMPORTANT]
> The result is **not** multiplied by the SPF value. In real life people apply much less sunscreen than the amount used in lab tests, so the app caps the estimate and always shows it as a guideline.

### Sunscreen timer

The timer is already working in the domain layer:

- A `SunscreenSession` stores when sunscreen was applied, the SPF and whether you're in the water. It lasts **2 hours**, or **40 minutes** in the water.
- `ObserveTimerStatusUseCase` combines the active session with a once-per-second tick and emits a `TimerStatus`:
  - `Idle`: no session
  - `Running(remaining, progress)`: before `endsAt`
  - `Finished(overdue)`: past `endsAt`, for up to 30 minutes, then it goes back to `Idle` on its own
- `TimerCardMapper` turns that status into what the Home timer card shows.
- For now sessions are kept in memory by `InMemorySunscreenRepository`, a `@Singleton` shared between ViewModels. Room will replace it.

Because the current time comes from an injected `java.time.Clock`, tests can use a fixed clock.

### Background work (planned)

| Worker | Type | What it does |
|---|---|---|
| `ForecastSyncWorker` | Periodic (every 3 h) | Refreshes the cache, updates the widget, schedules today's UV alert |
| `UvAlertWorker` | One-time | Notifies before the UV index reaches 6 or more |
| `ReapplyReminderWorker` | One-time (2 h or 40 min delay) | Reminds you to reapply sunscreen |

WorkManager doesn't fire at the exact minute. A few minutes of delay is fine for a sunscreen reminder, and it avoids the exact-alarm permission that Android 14 restricts.

## Permissions

Only `INTERNET` is declared right now. The others will be added together with their features.

| Permission | Why |
|---|---|
| `ACCESS_COARSE_LOCATION` | UV index for your area. City-level precision is enough, so precise location is never requested. |
| `POST_NOTIFICATIONS` | UV alerts and sunscreen reminders. Requested only when you turn on your first reminder (Android 13+). |
| `INTERNET` | Forecast data and sign-in. |

If you deny location access, you can choose a city manually.

## Privacy

- Your **account** (email and password) is managed by Supabase Auth.
- Your **skin type, sunscreen log and exposure history stay on your device**. They are never sent to a server.
- Your location is used only to request the forecast and is not stored.

## Roadmap

- [x] Multi-module project setup (`app`, `presentation`, `domain`, `data`, `core`)
- [x] Design system: colours, light and dark theme
- [x] Splash screen with animated logo
- [x] Hilt dependency injection
- [x] Type-safe navigation with a bottom navigation bar (4 tabs)
- [x] Shared component library in `core`
- [x] Login, register and skin type onboarding UI
- [x] Home screen UI with loading, error and offline states
- [x] Forecast screen UI (hourly chart, 5-day cards)
- [x] Sunscreen timer UI and timer logic (`ObserveTimerStatusUseCase`)
- [x] Profile screen UI with levels and patches
- [x] Open-Meteo client, DTOs and domain mapper, with unit tests
- [ ] Weather repository and connecting forecast data to Home and Forecast
- [ ] CI with GitHub Actions
- [ ] Room cache and offline mode
- [ ] Location with permission handling
- [ ] Supabase login and register
- [ ] Save the skin type (DataStore) and calculate safe exposure time
- [ ] UV alerts and sunscreen reminders (WorkManager)
- [ ] Glance widget
- [ ] Levels and patches logic
- [ ] Accessibility, EN/PT translations
- [ ] Release build on GitHub Releases

## Disclaimer

> [!WARNING]
> Sombra gives general guidance based on public weather data. It is not medical advice. If you have concerns about your skin, talk to a dermatologist.

## Author

**Diogo Marques** · [github.com/DMarques24](https://github.com/DMarques24)

## License

Copyright © 2026 Diogo Marques. All rights reserved.

The source code is public so it can be viewed and reviewed. You may not copy, modify or distribute it without permission.
