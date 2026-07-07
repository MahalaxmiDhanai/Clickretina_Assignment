# Skillforge

A production-quality Android learning app built in Kotlin with Jetpack Compose. Browse skill categories, explore courses, and dive into individual lessons with a mock video player experience.

## Screenshots

> Build the app and run it on an emulator or device to see the three screens in action.  
> (Screen recording: attach as `screen_recording.mp4` after filming on the device)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.x |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose 2.9 |
| Networking | Retrofit 2 + OkHttp + Moshi |
| Images | Coil 2 (`coil-compose`) |
| Architecture | MVVM — `data` / `ui` / `theme` |
| DI | Manual constructor injection (no Hilt) |
| Testing | JUnit 4 + kotlinx-coroutines-test |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |

---

## Project Structure

```
app/src/main/java/com/example/skillforge/
  data/
    model/         SkillforgeModels.kt    (Category, Course, Instructor, Lesson)
    remote/        SkillforgeApi.kt, RetrofitInstance.kt
    repository/    SkillforgeRepository.kt
  ui/
    home/          HomeScreen.kt, HomeViewModel.kt
    coursedetail/  CourseDetailScreen.kt, CourseDetailViewModel.kt
    lesson/        LessonScreen.kt, LessonViewModel.kt
    components/    CourseCard, CategoryChip, LessonRow, LoadingView, ErrorView
    navigation/    SkillforgeNavHost.kt, Routes.kt
    UiState.kt
  theme/           Color.kt, Type.kt, Theme.kt
  MainActivity.kt
```

---

## How to Build and Run

### Prerequisites
- Android Studio Ladybug (2024.2.x) or newer
- Android SDK 36 installed
- Java 17+

### Build the debug APK
```bash
./gradlew assembleDebug
```

The APK will be output to:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Run unit tests
```bash
./gradlew test
```

### Install directly to a connected device / emulator
```bash
./gradlew installDebug
```

---

## API

All data comes from a single endpoint:
```
GET https://raw.githubusercontent.com/android-assesment/notes/refs/heads/main/data.json
```

The response is cached in-memory per session (`SkillforgeRepositoryImpl`) so only one network call is made regardless of how many screens are open.

---

## UI State Pattern

Every screen uses a shared sealed class:
```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

`Loading` → shows `CircularProgressIndicator`  
`Success` → renders the screen content with a fade-in animation  
`Error` → shows message + a **Retry** button that re-triggers the network call

---

## How I Used AI

### Tools used
- **Google Gemini / Antigravity** (this session)
- **Cursor** (for local Compose syntax lookups during manual iteration)

### Actual prompts used (representative selection)

**Prompt 1:**
> "Build a complete Android Skillforge app in Kotlin with Jetpack Compose, Navigation Compose, Retrofit2 + Moshi, and Coil. Use MVVM with a sealed UiState class. The API is GET https://raw.githubusercontent.com/... Return all source files with full package paths."

**Prompt 2:**
> "The navigation currently uses Navigation3. Replace it with standard Navigation Compose 2.9 using `NavHost` and string-based routes. Keep the manual DI approach with a shared repository singleton."

**Prompt 3:**
> "Write at least 4 real JUnit4 unit tests for the HomeViewModel using a FakeSuccessRepository and a FakeErrorRepository. Tests should assert Success state fields (category name, course rating, lesson isFree), and Error propagation. Use StandardTestDispatcher and runTest."

### What AI got right
The Compose UI structure for all three screens (especially `LessonScreen` with the tab row + lesson list highlighting, and `CourseDetailScreen` with the hero image + stats chips) was generated cleanly and idiomatic on the first pass. The sealed `UiState` pattern and ViewModel factory wiring were also correct from the start.

### What AI got wrong — and how it was fixed
**Problem:** The initial scaffold used **Navigation3** (a newer alpha library) as the default template, but the navigation code written used the stable `androidx.navigation.compose` composable DSL — resulting in a dependency mismatch where `NavHost` and `composable {}` DSL weren't available.

**Fix:** Explicitly replaced the `nav3Core` + `lifecycleViewmodelNav3` dependencies in `libs.versions.toml` with `androidx.navigation:navigation-compose:2.9.0`, and removed the three Navigation3 implementation lines from `app/build.gradle.kts`. This brought the navigation back to the well-known stable API.

---

## Design Decisions

- **Light theme** with cream background (`#FBF6EE`) and teal accent (`#0F766E`)
- **Plus Jakarta Sans** bundled as static TTF files in `res/font/`
- **Category chips** double as filters — tapping a chip on Home filters the course list to that category
- **Lesson screen** swaps the current lesson in place (no re-navigation) when you tap another lesson in the list
- **Follow button** on Course Detail toggles between Follow / Following with local state only
- No Hilt, no Room, no WorkManager — intentionally lightweight per the assignment scope

---

## License

MIT — submitted as a take-home assignment for Clickretina.
