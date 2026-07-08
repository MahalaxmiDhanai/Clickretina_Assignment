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

#### Scaffold Prompt
> "Write a clean Kotlin codebase for a 3-screen Jetpack Compose app named Skillforge under package com.clickretina.skillforge. Retrofit must load the catalog from https://raw.githubusercontent.com/android-assesment/notes/refs/heads/main/data.json. Cache the response in a repository so we only fetch once on launch. The screens must be a Home list, Course Detail screen, and Lesson player screen. Do not use complex DI tools like Hilt or local DBs; keep the files focused."

#### Spec Refinement Prompt
> "The visual mocks require specific formats. Let's make sure the lesson row shows a PRICE tag instead of LOCKED. The duration hours must be 6.5h and 9h (strip trailing .0 on integers). The student count must be comma-formatted (e.g. 18,420). Write a code diff that corrects these formats across the card elements and the detail lists."

#### Layout and Navigation Update
> "Make all category card boxes in the scroll row exactly 148.dp by 148.dp so they are uniform, even if the titles wrap to two lines. Also, wire the See all buttons in HomeScreen to reset the active category filter, restoring the list to all 6 courses."

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
