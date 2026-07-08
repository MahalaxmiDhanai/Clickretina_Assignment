# Skillforge — Clickretina Android Take-Home

A premium native Android application built using **Kotlin + Jetpack Compose** that displays a complete, interactive course catalog fetched from a live REST API.

This project was built using an **AI-first orchestration methodology**, demonstrating how a senior engineer can leverage modern LLMs to scaffold, refine, and deliver production-ready code with speed and high fidelity.

---

## 📱 App Overview & Architecture

Skillforge is structured strictly under the **MVVM (Model-View-ViewModel)** architectural pattern, separating data operations from the visual presentation layer:

```
app/src/main/java/com/clickretina/skillforge/
├── data/
│   ├── model/          # Moshi parsed models (SkillforgeModels.kt)
│   ├── remote/         # Retrofit interface + instance config
│   └── repository/     # Caching repository interface + implementation
├── theme/              # Typography (Plus Jakarta Sans), dynamic color palettes, AppTheme
├── ui/
│   ├── components/     # Reusable UI widgets (CategoryChip, CourseCard, LessonRow, etc.)
│   ├── home/           # HomeScreen layout + StateFlow HomeViewModel
│   ├── coursedetail/   # CourseDetailScreen layout + CourseDetailViewModel
│   ├── lesson/         # LessonScreen (Mock Video Player) + LessonViewModel
│   └── navigation/     # NavHost (SkillforgeNavHost)
└── MainActivity.kt
```

### Key Technical Specs
- **Min SDK:** 24 (Target SDK: 36)
- **Networking:** Retrofit2 + OkHttp + Moshi (KSP JSON adapter code-gen)
- **Image Loader:** Coil (`AsyncImage` with content scale crop)
- **Font Face:** `Plus Jakarta Sans` (matching target layout)
- **Local State Caching:** In-memory mutex-locked repository cache (single API call, no redundant fetches)

---

## 🤖 AI Orchestration & Prompt Engineering Strategy

To build this app efficiently and meet the strict spec parameters, I utilized an **iterative prompting strategy**—combining a comprehensive master prompt for scaffolding with specialized refactoring prompts for layout and styling verification.

### 1. The Scaffolding Master Prompt
Rather than asking for files piecemeal, I structured a single architectural prompt that defined the constraints, API structure, and data layer expectations clearly to the AI model:

> **System Context:** You are a Senior Android Engineer building a take-home app named "Skillforge".
> 
> **Technology Constraints:**
> - Language: Kotlin (100% Compose, no XML layouts).
> - Navigation: Navigation Compose.
> - Network: Retrofit2 + Moshi (Moshi JSON code-gen serialization).
> - Target API Endpoint: `https://raw.githubusercontent.com/android-assesment/notes/refs/heads/main/data.json`
> - Architecture: Clean MVVM. UI State must be represented as a sealed class: `Loading`, `Success(data)`, and `Error(message)`.
> 
> **Core Architectural Goals:**
> 1. Fetch the data exactly once on start. Cache the response in the repository.
> 2. Pass IDs over Navigation routes; resolve records from the repository cache to avoid duplicate API requests.
> 3. Implement 3 screens: Home (with category horizontal list and courses vertical list), Course Detail (featuring tag chips, stats row, description, instructor bio, and follow button), and Lesson Screen (containing a mock video player with stateful play/pause, elapsed timeline calculations, and tabs).

---

### 2. Design Refinement & Spec Alignment Prompt
After the initial MVVM scaffolding was built, I ran a layout analysis check. I noted three specific formatting mismatches against the target design mock and prompted the AI to resolve them:

> **Design Alignment Task:** Let's refine the layouts to match the visual spec constraints exactly. Write a code modification that applies these rules:
> 
> 1. **Pill Tags:** Ensure free lessons show a green `FREE` tag, but paid lessons show a grey tag labeled `PRICE` (not `LOCKED`).
> 2. **Numeric Formats:** Student enrollment numbers must be fully comma-separated integers (`%,d` format, e.g., `18,420`), never abbreviated (no `18.4k`).
> 3. **Time Formatting:** Course durations must display with decimal hours for partial fractions (e.g., `6.5h`), but drop the decimal if it is a whole number (e.g., `9h` instead of `9.0h`).
> 4. **Layout Alignment:** Force the Category Chip containers to use a fixed square size (`148.dp` x `148.dp`) with a maximum text limit of 2 lines and ellipsis overflow, preventing wrapped strings from causing uneven card heights in the LazyRow.

---

## 🔍 AI Evaluation: Co-Pilot Analysis

### 🎯 What the AI Got Right
- **Boilerplate and Routing:** The navigation host setup, Retrofit API client, and Moshi model generation were 100% correct on the first pass. This eliminated manual setup time for network modules.
- **State Management:** Emitting UI states through Kotlin `StateFlow` and collecting them in the Compose view layer using `.collectAsState()` was cleanly implemented.

### 🛠️ Where I Had to Intervene (Human-in-the-Loop)
- **Emulator Network Quirks:** The AI could not resolve simulated network routing timeouts on the local emulator. I manually resolved this by updating the emulator configurations and applying direct DNS settings to ensure clean API calls.
- **Spec Verification:** The AI initially fell back to standard learning app conventions (using `"LOCKED"` instead of `"PRICE"` for paid courses). I corrected this logic in `LessonRow.kt` and `CategoryChip.kt` to ensure compliance with the specific take-home rubric.

---

## 🧪 Testing and Verification

To guarantee stability, the repository contains robust unit tests in `HomeViewModelTest.kt`:
- **Repository Success States:** Validates catalog loads and maps categories correctly.
- **Course Field Mappings:** Verifies levels, durations, and rating strings are parsed properly.
- **Lesson Access Rules:** Checks correct evaluation of the `isFree` boolean property across lesson elements.
- **Failure Handling:** Asserts the view model propagates network errors cleanly to the UI layer.

### How to Run Tests
```bash
./gradlew test
```
*(All tests pass successfully with green reports)*
