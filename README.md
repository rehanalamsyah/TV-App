# TVApp — TV Show Browser

A simple Android TV show browser built with **Kotlin + Jetpack Compose**, using the [TVMaze public API](https://www.tvmaze.com/api).

> 🎬 Video walkthrough: *[Link to be added after recording]*

---

## Features

- 📺 **List Screen** — Browse ~250 TV shows in a 2-column grid with poster, title, and rating
- 📄 **Detail Screen** — Full show detail with large poster, premiere date, summary (HTML rendered), and share action
- ⚡ **3 UI States** — Loading spinner, Error with Retry button, Success content
- 🔗 **Share** — Share show title, summary and URL via Android share sheet

---

## How to Run

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK API 24+
- Java 11 (configured in `compileOptions`)

### Steps
1. Clone the repository
   ```bash
   git clone <repo-url>
   cd TVApp
   ```
2. Open the project in Android Studio
3. Wait for Gradle sync to complete (dependencies will be downloaded automatically)
4. Run on an emulator or physical device (API 24+):
   - Click **Run ▶** or press `Shift+F10`
   - Or via CLI: `./gradlew installDebug`

### Run Tests
```bash
./gradlew test
```

---

## Architecture

The app follows **MVVM (Model-View-ViewModel)** with a Repository pattern.

```
com.dicoding.tvapp/
├── data/
│   ├── model/              # Data classes: TvShow, TvShowDetail, Image, Rating
│   ├── remote/             # Retrofit interface (TvMazeApiService) + singleton (RetrofitInstance)
│   └── repository/         # TvShowRepository — wraps API calls with Result<T>
├── ui/
│   ├── list/               # ShowListViewModel + ShowListScreen
│   ├── detail/             # ShowDetailViewModel + ShowDetailScreen
│   └── theme/              # Material3 theme (Color, Type, Theme)
├── navigation/             # NavGraph.kt — Compose Navigation with sealed Screen routes
└── MainActivity.kt         # Entry point, sets up NavHost
```

### Key Decisions

| Decision | Rationale |
|---|---|
| **Jetpack Compose** | Modern declarative UI, less boilerplate than XML |
| **MVVM + Repository** | Separation of concerns; ViewModel survives config changes |
| **Retrofit + Gson** | Battle-tested HTTP client; simple JSON mapping |
| **Coil** | Compose-native image loading; handles caching automatically |
| **Navigation Compose** | Type-safe routes with sealed classes; no fragment transactions |
| **StateFlow** | Lifecycle-safe state emission to Composables |
| **Result<T>** | Kotlin stdlib; avoids custom sealed class for repository layer |

### HTML Summary Handling
The TVMaze API returns summaries with HTML tags (`<p>`, `<b>`, etc.). These are stripped using `HtmlCompat.fromHtml()` from `androidx.core.text` — no external library needed.

---

## What I'd Improve with More Time

1. **Pagination** — Implement infinite scroll / load-more for all ~50,000 shows (currently loads page 0 only)
2. **Hilt Dependency Injection** — Replace constructor injection with Hilt for cleaner testability
3. **Offline Caching** — Add Room database to cache shows for offline browsing
4. **Season/Episode/Cast** — The bonus detail: fetch episodes per season and cast from TVMaze
5. **Better Error UX** — Distinguish network errors from server errors with specific messages
6. **Search** — Real-time search via `GET /search/shows?q=query`
7. **Shimmer Loading** — Replace spinner with skeleton loading cards for better perceived performance
8. **Instrumented UI Tests** — Add Compose test rules for end-to-end screen testing

---

## Dependencies

| Library | Version | Purpose |
|---|---|---|
| Retrofit | 2.11.0 | HTTP client |
| OkHttp Logging | 4.12.0 | Debug logging |
| Gson | 2.11.0 | JSON parsing |
| Coil Compose | 2.7.0 | Image loading |
| Navigation Compose | 2.9.0 | Screen navigation |
| ViewModel Compose | 2.10.0 | MVVM |
| Coroutines | 1.10.2 | Async operations |
