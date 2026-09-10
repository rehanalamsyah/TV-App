# AI Usage Log

AI tools used: **Antigravity (Gemini)** — used for generating the full app scaffold, then reviewed and modified each output.

---

## Entry 1

**What I asked:** "Generate a Retrofit API service interface for the TVMaze API with endpoints for getting all shows (with page parameter) and getting a single show by ID"

**What it gave me:** A clean interface with `@GET` annotations, `suspend` functions, and `@Query`/`@Path` parameters — essentially correct.

**What I did:** Accepted with minor modification — added `= 0` default value to the `page` parameter so callers don't need to always pass it explicitly.

**One thing the AI got wrong / I verified:** The AI initially used `Call<List<TvShow>>` (non-coroutine style) in its first draft. I corrected this to `suspend fun ... : List<TvShow>` since the project uses Kotlin coroutines throughout and `suspend` is cleaner with `viewModelScope.launch`.

---

## Entry 2

**What I asked:** "Generate a ShowListViewModel using StateFlow, a sealed UiState, and a TvShowRepository dependency injected via constructor"

**What it gave me:** A ViewModel with `MutableStateFlow`, sealed class `ShowListUiState`, `viewModelScope.launch`, and `init { loadShows() }`.

**What I did:** Accepted, but added a default parameter to the constructor: `repository: TvShowRepository = TvShowRepository(RetrofitInstance.api)`. This allows Android's default `viewModel()` factory to construct it without Hilt while still being injectable in tests.

**One thing the AI got wrong / I verified:** The AI's error handling used `.catch {}` on a Flow — but the repository returns `Result<T>` from a single `suspend` call, not a Flow. I switched to `.onSuccess` / `.onFailure` on the `Result` directly, which is simpler and more idiomatic.

---

## Entry 3

**What I asked:** "Write a Compose screen for the show list with a 2-column LazyVerticalGrid, each card showing the poster image (Coil AsyncImage), title, and rating. Handle all 3 states."

**What it gave me:** A complete `ShowListScreen` composable with all 3 states. The structure was good.

**What I did:** Modified the rating display — the AI showed `rating.average.toString()` without null checking, which would display `"null"` for shows with no rating. I changed it to `show.rating?.average?.let { "⭐ $it" } ?: "No rating"`.

**One thing the AI got wrong / I verified:** The AI set `aspectRatio(16f/9f)` for poster images. TV show posters are portrait (roughly 2:3), so a 16:9 ratio made images look very squished. I changed it to `aspectRatio(2f / 3f)` to match actual poster proportions.

---

## Entry 4

**What I asked:** "Handle HTML tags in the TVMaze summary field (contains `<p>`, `<b>` tags). Show plain text in Compose."

**What it gave me:** Suggested using `HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()` from `androidx.core.text`.

**What I did:** Accepted as-is. Added `.trim()` to remove leading/trailing whitespace and newlines that `fromHtml` often leaves.

**One thing the AI got wrong / I verified:** The AI suggested adding the `jsoup` library as an external dependency to parse HTML. I rejected this — `HtmlCompat` is already available via `androidx.core` which the project already depends on. Adding jsoup would be an unnecessary dependency for a simple tag-stripping task.

---

## Entry 5

**What I asked:** "Write the share Intent for Android — share the show's title, summary (plain text), and URL"

**What it gave me:** An `Intent(Intent.ACTION_SEND)` with `type = "text/plain"`, `EXTRA_TEXT`, and `EXTRA_SUBJECT`.

**What I did:** Accepted. Added `buildString {}` to format the share content neatly with line breaks and an emoji for readability.

**One thing the AI got wrong / I verified:** The AI used `startActivity(intent)` without `Intent.createChooser()`. Without `createChooser`, Android may auto-select the last used sharing app instead of showing the chooser dialog. I wrapped it with `Intent.createChooser(intent, "Share via")` to always show the sheet.

---

## Entry 6

**What I asked:** "Write unit tests for ShowListViewModel — test success state, error state, and null rating handling"

**What it gave me:** Tests using `runTest`, `StandardTestDispatcher`, `advanceUntilIdle()`, and `mockito-kotlin`.

**What I did:** Accepted the structure. Added a 4th test for the `initial Loading state` to verify that the ViewModel correctly emits `Loading` before the coroutine runs. Also added the `InstantTaskExecutorRule` which the AI omitted — required when using `LiveData` or `arch.core:core-testing` in tests.

**One thing the AI got wrong / I verified:** The AI's tests called `Dispatchers.setMain(testDispatcher)` but forgot `Dispatchers.resetMain()` in `@After`. Without tearDown, test dispatcher leaks between tests and can cause flaky test failures. I added it in the `tearDown()` method.

---

## Entry 7

**What I asked:** "Write the CODE_REVIEW.md reviewing the provided AI-generated Kotlin ViewModel snippet"

**What it gave me:** A structured review pointing out threading, state exposure, and error handling issues.

**What I did:** Used it as a starting point but added more nuance — specifically noting that `var movies` being `public var` violates encapsulation (should be `private val _movies` with a public `val movies` read-only exposed), which the AI missed in its initial review.

**One thing the AI got wrong / I verified:** The AI said `url.readText()` "runs on the main thread" without explaining *why* this crashes (Android throws `NetworkOnMainThreadException` since API 9). I expanded the explanation to include the actual exception name so a reviewer knows what error to look for in logcat.
