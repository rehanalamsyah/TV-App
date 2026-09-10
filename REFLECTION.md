# Reflection

---

## 1. Which part of your submission are you least confident about, and why?

The **unit tests**. While the tests cover the ViewModel's success, error, and edge case states correctly, I'm least confident about the test setup — specifically the interaction between `Dispatchers.setMain(testDispatcher)`, `advanceUntilIdle()`, and how coroutine timing affects what state the ViewModel is in at assertion time.

Coroutine-based testing has subtle ordering issues: if you assert state *before* `advanceUntilIdle()`, you get the `Loading` state; if after, you get the final state. Getting this right required careful thought and verification. I'm confident the tests pass, but I would want a more experienced reviewer to confirm the test structure is idiomatic and won't become flaky on different Kotlin/coroutines versions.

---

## 2. Describe a moment during this project (or any past project) where you got completely stuck. What did you do, step by step?

During this project, I got stuck when navigation from the list to detail screen wasn't passing the show ID correctly — the detail screen was always loading the wrong show or crashing.

Step by step:
1. I added `Log.d` statements at the navigation call site and inside `ShowDetailScreen` to print the received ID
2. I noticed the ID was being passed as a `String` from the route but the NavArgument expected `NavType.IntType` — causing a silent type mismatch
3. I read the Navigation Compose documentation on `navArgument` types more carefully
4. I updated the argument definition to explicitly set `type = NavType.IntType` and verified the route string format `"detail/{showId}"` matched the composable's argument name exactly
5. Retested and confirmed the correct show loaded

The key lesson: always verify the type contract between route arguments and NavType declarations — Compose Navigation doesn't always give helpful error messages for type mismatches.

---

## 3. It's Thursday, your task is due Friday, and you realize you misunderstood the requirement — half your work is wrong. What are you doing now?

1. **Stay calm and assess scope** — I'd first quickly map exactly what's wrong vs. what's still salvageable, to understand the true scope of the fix.
2. **Communicate immediately** — I'd tell my supervisor/mentor right away: "I realized I misunderstood X. Here's what's correct. I can fix Y by tomorrow morning. Should I prioritize that over Z?" Hiding it until Friday is always worse.
3. **Cut scope ruthlessly** — Drop any non-essential features and focus only on what was misunderstood. Deliver the core requirement correctly rather than everything done poorly.
4. **Work tonight if needed** — If the fix is within reach, I'd work the evening to close the gap.
5. **Document what's incomplete** — If I can't finish everything, I'd write clear notes about what's pending and why, so the reviewer understands the state of the submission.

The goal is to deliver something honest and functional, not to pretend everything is fine.

---

## 4. Your mentor asks you to change an approach you believe is worse. What do you do?

I'd follow this process:

1. **Ask to understand their reasoning first** — They might have context I don't (performance constraints, team conventions, future plans). Maybe their approach *is* better once I understand the full picture.
2. **Share my concern respectfully** — If I still disagree, I'd say something like: "I see the value of your approach. My concern is [specific technical reason]. Could we discuss the trade-offs?" — not "your way is wrong."
3. **Propose a quick test or comparison** — If feasible, offer to prototype both approaches and compare results objectively.
4. **Defer and learn** — If they still prefer their approach after discussion, I'd implement it their way. They're the mentor; they may be teaching me something I can only understand through experience. I'd take a mental note to revisit the decision in retrospect.
5. **Document the decision** — I'd note in a comment or PR description *why* the approach was chosen, so the rationale is preserved for future readers.

The key is: disagree professionally through dialogue, then commit fully once a decision is made.

---

## 5. What's something technical you taught yourself recently outside of class/work, and how did you learn it?

I taught myself **Jetpack Compose** beyond what was covered in coursework. My course covered XML layouts, but the industry was clearly shifting to Compose.

How I learned it:
1. Read the official [Compose pathway](https://developer.android.com/courses/pathways/compose) on developer.android.com — the codelabs are well-structured
2. Built small throwaway apps to practice each concept: state hoisting, `LazyColumn`, `ViewModel` + `StateFlow`, navigation
3. When I got confused about recomposition (why was my UI not updating?), I read the [thinking in Compose](https://developer.android.com/develop/ui/compose/mental-model) article which clarified the mental model
4. Reviewed open-source Compose apps on GitHub (JetNews, Now In Android) to see production-level patterns

The biggest "aha" moment was understanding that **state drives UI** — Compose re-renders only when state changes, not imperatively. Once that clicked, the rest followed naturally.
