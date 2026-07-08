# Skillforge — Clickretina Android Assignment

A native Android app built in **Kotlin + Jetpack Compose** that displays a catalog of skill-based courses fetched from a live REST API.

---

## 📱 Screens

| Screen | Description |
|---|---|
| **Home** | Welcome header, search bar, horizontal category chips (tap to filter), full course card list |
| **Course Detail** | Hero banner, tags, stat row, instructor + Follow toggle, lesson list with FREE / PRICE pills |
| **Lesson Player** | Mock video player with play/pause, live progress bar, timestamp, tabs (Lessons · Notes · Resources) |

---

## 🚀 How to Run

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 24+
- Internet connection (loads live data from GitHub raw JSON)

### Build & Run
```bash
# Debug APK
./gradlew assembleDebug
# APK location: app/build/outputs/apk/debug/app-debug.apk

# Unit tests
./gradlew test
```

Or open in Android Studio → Run → ▶

---

## 🏗️ Tech Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose |
| Navigation | Navigation Compose |
| Networking | Retrofit2 + OkHttp |
| JSON | Moshi (KSP code-gen) |
| Images | Coil |
| Architecture | MVVM + StateFlow |
| Tests | JUnit4 + Coroutines Test |

---

## 🔗 API

```
GET https://raw.githubusercontent.com/android-assesment/notes/refs/heads/main/data.json
```

Single call on app start, cached in memory. Navigation passes IDs only — no redundant network calls.

---

## 🤖 How I Worked With AI

### Tools Used
- **Antigravity (Google DeepMind)** — primary AI coding assistant used throughout the build

---

### Actual Prompts I Sent

**Prompt 1 — Project scaffolding:**
> "Build a complete, working Android app called Skillforge in Kotlin, from scratch. Use Jetpack Compose, Retrofit2, Moshi, Coil, Navigation Compose, and MVVM. The API is https://raw.githubusercontent.com/android-assesment/notes/refs/heads/main/data.json. Build exactly 3 screens: Home, Course Detail, Lesson Player (mock, no real video). Package name must be com.clickretina.skillforge."

**Prompt 2 — Data formatting correctness:**
> "Make sure duration hours show as 6.5h and 9h — not 9.0h. Students enrolled must show as 18,420 with a comma, not abbreviated as 18.4k. The paid lesson pill label must be PRICE, not LOCKED."

**Prompt 3 — Category filtering:**
> "Whenever I click on a category chip, filter the popular courses list to show only that category's courses. When I click See all, show all 6 courses again. The See all button under both the Categories section and the Popular courses section should both clear the filter."

---

### ✅ One Thing AI Got Right

The AI correctly set up the entire MVVM architecture — `UiState` sealed class (Loading / Success / Error), `StateFlow` in each ViewModel, Retrofit + Moshi data models matching every field in the JSON exactly, and the Navigation Compose route graph with typed arguments (`categoryId`, `courseId`, `lessonId`). This would have taken hours manually and was generated accurately in one pass.

---

### ❌ One Thing AI Got Wrong — And How I Fixed It

The AI initially labeled paid lessons with the text **`"LOCKED"`** instead of **`"PRICE"`** as required by the spec. It also formatted student counts as **`"18.4k"`** (abbreviated) instead of **`"18,420"`** (comma-formatted integer), and displayed whole-number durations as **`"9.0h"`** instead of **`"9h"`**.

**How I fixed it:**
- In [`LessonRow.kt`](app/src/main/java/com/clickretina/skillforge/ui/components/LessonRow.kt): changed `"LOCKED"` → `"PRICE"` in `FreeLockPill`
- In [`CourseDetailScreen.kt`](app/src/main/java/com/clickretina/skillforge/ui/coursedetail/CourseDetailScreen.kt): added `"%,d".format(students)` for comma-formatted counts
- In both `CourseCard.kt` and `CourseDetailScreen.kt`: added the check `if (hours % 1.0 == 0.0) "${hours.toInt()}h" else "${hours}h"` to strip trailing `.0`

These were spec-critical details that the AI missed, requiring a direct code review against the live API response.

---

## 📂 Project Structure

```
app/src/main/java/com/clickretina/skillforge/
├── data/
│   ├── model/          # Data classes (SkillforgeModels.kt)
│   ├── remote/         # Retrofit API + instance
│   └── repository/     # SkillforgeRepository interface + impl
├── theme/              # Colors, typography, Theme.kt
├── ui/
│   ├── home/           # HomeScreen + HomeViewModel
│   ├── coursedetail/   # CourseDetailScreen + CourseDetailViewModel
│   ├── lesson/         # LessonScreen + LessonViewModel
│   ├── components/     # CourseCard, CategoryChip, LessonRow, etc.
│   └── navigation/     # SkillforgeNavHost
└── MainActivity.kt
```

---

## ✅ Assignment Checklist

- [x] 3 screens exactly as spec: Home, Course Detail, Lesson Player
- [x] Single API call, in-memory cache
- [x] Category filter chips (tap to filter, tap again to deselect)
- [x] "See all" resets filter to show all 6 courses
- [x] FREE / PRICE lesson pills (from `isFree` field)
- [x] Duration: `6.5h` / `9h` (no trailing `.0`)
- [x] Students: `18,420` (full comma-formatted integer)
- [x] Instructor Follow / Following toggle (local state)
- [x] Mock video player with play/pause + live progress simulation
- [x] Lesson sidebar switching (active lesson highlight in-place)
- [x] Loading + Error + Retry states on all screens
- [x] 4 JUnit unit tests — all passing
- [x] `assembleDebug` → clean APK
- [x] Package: `com.clickretina.skillforge`
