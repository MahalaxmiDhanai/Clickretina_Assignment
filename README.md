# Skillforge — Clickretina Android Assignment

Skillforge is a native Android application built in Kotlin and Jetpack Compose that displays a catalog of courses and lessons fetched from a live remote JSON API.

---

## Core Features and Specs

- **Home Screen**: Features category horizontal filtering and popular courses vertical listings.
- **Course Detail Screen**: Displays course metadata, tags, dynamic stats (rating, student count, duration), an instructor follow toggle, and lesson rows.
- **Lesson Screen**: Contains a mock video player with play/pause controls, live timeline elapsed tracking, tab selections (Lessons, Notes, Resources), and in-place active lesson switching.

---

## Technical Stack

- **UI Framework**: Jetpack Compose
- **Navigation**: Navigation Compose (passing route parameters for screens)
- **Networking**: Retrofit2 with OkHttp Client
- **JSON Converter**: Moshi with KSP code-gen adapter
- **Image Loading**: Coil Image library
- **Architecture**: Model-View-ViewModel (MVVM) with StateFlow
- **Test Suite**: JUnit4 + Coroutines Test Dispatchers

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 24 or newer

### Build Instructions
To build the debug APK via CLI:
```bash
./gradlew assembleDebug
# The built APK will be located at: app/build/outputs/apk/debug/app-debug.apk
```

To run unit tests:
```bash
./gradlew test
```

---

## How I Worked with AI (Our Collaborative Process)

### Tools Used
- **Antigravity (Google DeepMind)** for initial generation, code refinement, and packaging updates.

### Prompts Sent

#### 1. The Architectural Scaffolding Master Prompt
To prevent the AI from generating generic boilerplate or hallucinating fields, I provided a highly structured system prompt specifying the data layer contract, dependencies, and state boundaries:

```
Act as a senior Android engineer. We are building "Skillforge" under package com.clickretina.skillforge.
Use Jetpack Compose, Navigation Compose, Retrofit2, Moshi, and Coil.
Implement a clean MVVM structure. Define UI states as a sealed class: Loading, Success, and Error.

Data Constraint:
The API is https://raw.githubusercontent.com/android-assesment/notes/refs/heads/main/data.json
Fetch the catalog exactly once on application startup. Store the response in an in-memory cache in the Repository.
When navigating between screens, only pass IDs (categoryId, courseId, lessonId) in the route path. ViewModels must resolve details from the cached Repository to avoid redundant API queries.

Screen Specs:
1. Home: Vertical LazyColumn. A horizontal LazyRow filters courses by category. Clicking a card navigates to Course Detail.
2. Course Detail: Banner image, dynamic tag chips, stats row, about text, instructor card (with Follow toggle), and lesson rows.
3. Lesson: A mock player container displaying elapsed timeline calculations, tab layout (Lessons, Notes, Resources), and a list of sibling lessons. Clicking a sibling lesson must switch the active player state in-place without reloading the screen.
```

#### 2. Spec Refinement and Formatting Prompt
To align the UI with the exact requirements of the take-home assessment, I sent a follow-up constraint prompt targeting the text formatting and UI labels:

```
Review the UI and apply these specific formatting rules:
- Paid lessons in the lesson list must display a grey tag labeled "PRICE" (do not use "LOCKED"). Free lessons show a green "FREE" tag.
- Student enrollments must be fully comma-separated (e.g., 18,420), never abbreviated as 18.4k.
- Course durations must drop trailing decimals for integers (e.g., show "9h" instead of "9.0h") but preserve them for halves (e.g., "6.5h").
- Lesson list eyebrow text above the player title must read: "LESSON N · COURSE TITLE" in uppercase.
Generate a code modification for LessonRow.kt, CourseCard.kt, and CourseDetailScreen.kt implementing these rules.
```

#### 3. Layout Sizing and Click Reset Prompt
During integration testing, I resolved uneven card dimensions and added "See all" functionality using this layout refactoring prompt:

```
Let's resolve the UI alignment in HomeScreen:
- When category names wrap to two lines, the CategoryChip cards display at different heights. Modify CategoryChip.kt to set a fixed size of 148.dp by 148.dp, set maxLines = 2, and use ellipsis overflow for the text.
- Modify the "See all" text buttons under both the Categories and Popular courses sections to be clickable. Clicking either must clear the active selectedCategoryId (reset to null) so all 6 popular courses are shown again.
```

---

### Challenges Faced and Human Interventions

#### 1. Formatting Mismatches (AI Hallucinations)
The AI originally generated standard learning app tags like "LOCKED" and abbreviated student metrics as "18.4k" based on common conventions. I had to manually review the target specifications and instruct the AI to use exact "PRICE" text tags, raw comma-separated formatting, and clean integer truncations for course durations.

#### 2. Layout Sizing Discrepancies
When category names wrapped ("Android Development" vs "Backend & APIs"), the cards in the LazyRow rendered at different heights. I corrected the chip dimensions to a fixed square layout (148.dp x 148.dp) with maximum line boundaries and ellipses overflow.

#### 3. Network Outages on the Local Emulator
During verification, the emulator environment lost its routing gateway, making the app fall back to error views. Rather than rewriting network libraries, I troubleshot the local Android virtual device, modified DNS settings, and restarted the ADB server to bring the connection back online.

#### 4. Package Refactoring
The project was originally scaffolded under a generic workspace package name. I directed a script to refactor all source imports, directories, namespaces, and build files to "com.clickretina.skillforge" and verified that all 4 unit tests compiled and passed.
