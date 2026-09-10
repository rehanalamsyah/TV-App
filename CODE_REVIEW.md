# Code Review — AI-Generated Kotlin ViewModel

Reviewing the following AI-generated code for an Android `ViewModel`:

```kotlin
class MovieViewModel : ViewModel() {
    var movies: List<Movie> = emptyList()

    fun loadMovies() {
        val url = URL("https://api.example.com/movies")
        val data = url.readText()
        movies = parseMovies(data)
    }
}
```

---

## Issue 1: Network call on the main thread → App crash

**Problem:** `url.readText()` is a blocking I/O call. When `loadMovies()` is called from the UI (e.g., a button click or `LaunchedEffect`), it runs on Android's **main thread**. Android will throw `NetworkOnMainThreadException` immediately and crash the app. This is enforced since API 9.

**Fix:** Move the network call to a background dispatcher using coroutines:

```kotlin
fun loadMovies() {
    viewModelScope.launch(Dispatchers.IO) {
        val data = URL("https://api.example.com/movies").readText()
        val parsed = parseMovies(data)
        withContext(Dispatchers.Main) {
            movies = parsed
        }
    }
}
```

Or better, use a proper HTTP client (Retrofit with suspend functions) that already handles threading.

---

## Issue 2: `var movies` is mutable and publicly exposed — breaks encapsulation

**Problem:** `var movies: List<Movie> = emptyList()` is `public var`. Any class can write to it (`viewModel.movies = someList`), which breaks unidirectional data flow and makes the state unpredictable.

**Fix:** Use the private-mutable / public-read pattern with `StateFlow`:

```kotlin
private val _movies = MutableStateFlow<List<Movie>>(emptyList())
val movies: StateFlow<List<Movie>> = _movies.asStateFlow()
```

This ensures only the ViewModel mutates state, and observers (UI) only read it.

---

## Issue 3: No error handling → silent crashes

**Problem:** If the network fails (no internet, server error, timeout), `url.readText()` throws an `IOException`. There is no `try/catch`, so the exception will propagate uncaught and crash the app (or silently fail if the coroutine swallows it).

**Fix:** Wrap in `try/catch` or use Kotlin's `runCatching`:

```kotlin
viewModelScope.launch(Dispatchers.IO) {
    runCatching {
        URL("https://api.example.com/movies").readText()
    }.onSuccess { data ->
        _uiState.value = UiState.Success(parseMovies(data))
    }.onFailure { error ->
        _uiState.value = UiState.Error(error.message ?: "Unknown error")
    }
}
```

---

## Issue 4: No loading / error UI states — poor UX

**Problem:** The ViewModel only holds the result list. There is no way for the UI to know if a load is in progress or failed. Users see a blank list with no feedback.

**Fix:** Introduce a sealed `UiState` class:

```kotlin
sealed class UiState {
    object Loading : UiState()
    data class Success(val movies: List<Movie>) : UiState()
    data class Error(val message: String) : UiState()
}

private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
val uiState: StateFlow<UiState> = _uiState.asStateFlow()
```

The UI can then show a spinner, error message with retry, or the actual content.

---

## Issue 5: Using raw `java.net.URL` — wrong tool for Android

**Problem:** `java.net.URL.readText()` works but is a primitive blocking call with no timeout configuration, no response code checking, no header support, and no retry logic. It will also succeed on HTTP 4xx/5xx responses without throwing — silently returning error HTML.

**Fix:** Use **Retrofit** (or OkHttp) which:
- Handles threading properly with `suspend`
- Throws `HttpException` for non-2xx responses
- Supports timeouts, interceptors, and JSON conversion

```kotlin
// With Retrofit + Gson
@GET("movies")
suspend fun getMovies(): List<Movie>

// In ViewModel
fun loadMovies() {
    viewModelScope.launch {
        runCatching { api.getMovies() }
            .onSuccess { _uiState.value = UiState.Success(it) }
            .onFailure { _uiState.value = UiState.Error(it.message ?: "Error") }
    }
}
```

---

## Issue 6: `parseMovies()` has no visible implementation or error handling

**Problem:** `parseMovies(data)` is called without any type information or null checks. If parsing fails (malformed JSON, unexpected schema), it will throw and crash. There is no indication of what format `data` is in.

**Fix:** Use a typed JSON parser (Gson, Moshi) with proper error handling:

```kotlin
runCatching {
    gson.fromJson(data, Array<Movie>::class.java).toList()
}.getOrElse { emptyList() }
```

---

## Summary Table

| # | Issue | Severity | Fix |
|---|---|---|---|
| 1 | Network on main thread | 🔴 Critical | Use `viewModelScope.launch(Dispatchers.IO)` or Retrofit suspend |
| 2 | Mutable public state | 🟠 High | `private val _movies: MutableStateFlow` + public `StateFlow` |
| 3 | No error handling | 🟠 High | `try/catch` or `runCatching` |
| 4 | No loading/error states | 🟡 Medium | Sealed `UiState` class |
| 5 | Raw `java.net.URL` | 🟡 Medium | Replace with Retrofit |
| 6 | Untyped `parseMovies()` | 🟡 Medium | Typed JSON parsing with error handling |
