# Top Ανέκδοτα: B4A to Kotlin Multiplatform Migration Summary

This file documents the architecture, implementation choices, and resolved compiler issues during the migration of the classic Basic4Android (B4A) app **Top Ανέκδοτα (Top Jokes)** to **Kotlin Multiplatform (KMP) & Compose Multiplatform (CMP)**.

---

## 1. Project Goals & Strategy
- **Target Platforms**: Android, iOS, Desktop (JVM), Web (JS & WasmJS).
- **Scope**: Exclude old advertisements and Pollfish integrations. Migrate all B4A category jokes files, fonts, and drawable resources to the KMP common main resources.
- **Data Persistence**: Store jokes locally in a SQLite database, pre-populated on the first launch of the application from read-only text resources.

---

## 2. Technical Architecture

### A. SQLite Database Layer (`androidx.sqlite:sqlite-bundled`)
- **Choice of API**: We implemented the lightweight multiplatform database layer directly using Google's low-level `androidx.sqlite:sqlite-bundled` driver. 
- **Rationale**: Attempting to use the Room compiler with KSP on the Kotlin Multiplatform Android Library target crashed during configuration (`ClassCastException` with Gradle's Kotlin Multiplatform Library plugin). Using direct SQLite driver connection APIs bypassed KSP/Room overhead and provided a 100% stable compiler build on all native platforms.
- **Web Support**: JS and WasmJS targets fall back to an in-memory/localStorage implementation of the database interface, which behaves exactly like the SQL database.

### B. expect/actual Settings (`KeyValueStorage`)
- Handles user preferences (selected font family, text size, color theme, and first-launch state).
- **Android**: `SharedPreferences`
- **iOS**: `NSUserDefaults`
- **JVM (Desktop)**: Java standard `Preferences`
- **JS / WasmJS (Web)**: Web `localStorage`

### C. Threading & Coroutines (`ioDispatcher`)
- **Problem**: `Dispatchers.IO` is unavailable on Web targets (JS/WasmJS) and restricted on native iOS targets in certain compiler/coroutines configurations.
- **Solution**: We created a custom expect/actual `ioDispatcher` in `commonMain`:
  - **Android / JVM (Desktop)**: actualizes to `Dispatchers.IO`
  - **iOS / JS / WasmJS**: actualizes to `Dispatchers.Default` (which maps to background worker threads on iOS and handles the single event loop on Web).

---

## 3. Key Dependencies & Configurations

### Kotlin and Compose Alignments (`gradle/libs.versions.toml`)
- **Kotlin Version**: `2.4.0` (required by the project template and Compose Multiplatform `1.11.1`).
- **Compose Multiplatform**: `1.11.1`
- **Material Extended Icons**: 
  - JetBrains no longer publishes updating `material-icons-extended` libraries.
  - The correct KMP-supported artifact is **`org.jetbrains.compose.material:material-icons-extended:1.7.3`**. Using this version resolves the dependency across all targets (including iOS, JS, and WasmJS).
- **Activity Compose**:
  - Added `androidx-activity-compose` to `androidMain.dependencies` in `shared/build.gradle.kts` to resolve `PlatformBackHandler` system gestures on Android.

---

## 4. Key Implementation Details & Gotchas

### SQLite Statement API Mismatches
1. **Bundled SQLite Package**: The correct package for the bundled driver is `androidx.sqlite.driver.bundled.BundledSQLiteDriver` (not `androidx.sqlite.driver.BundledSQLiteDriver`).
2. **Column String Extraction**: The `androidx.sqlite` interface uses **`stmt.getText(index)`** rather than `getString(index)` to read text columns.
3. **Statement Resource Management**: Because of differences in standard library auto-closeable mappings across native compilers, we wrote a custom inline helper `use` extension function for `SQLiteStatement` to handle query statement closing reliably:
   ```kotlin
   inline fun <R> SQLiteStatement.use(block: (SQLiteStatement) -> R): R {
       try {
           return block(this)
       } finally {
           close()
       }
   }
   ```

### iOS Native API Opt-In
- Calls to iOS platform APIs (like `NSFileManager.defaultManager.URLForDirectory`) inside `iosMain` require annotation with `@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)`.

---

## 5. Verification & Running Commands

To compile all targets in the shared library, use:
```bash
./gradlew :shared:assemble
```

To run the Desktop (JVM) client:
```bash
./gradlew :desktopApp:run
```

To stop memory-intensive Gradle daemons in case of high heap usage:
```bash
./gradlew --stop
```
*(Max heap settings have been increased in `gradle.properties` to `6GB` for Gradle and `4GB` for the Kotlin compiler daemon).*

---

## 6. Category Selection UI & Gesture Conflict Resolutions

### A. Sizing & Sizing Ratios (Aspect Ratio Layout)
- **Problem**: The category sliding panel images were distorted or cropped due to basic Compose bounds and padding constraints.
- **Solution**: 
  - Aligned the `HorizontalPager` to take full screen width (`contentPadding = PaddingValues(0.dp)`) and sized its height centered in the layout via `Modifier.fillMaxWidth().aspectRatio(680f / 440f)`.
  - Removed Card curves and borders (`shape = RoundedCornerShape(0.dp)`, `elevation = 0.dp`) to replicate B4A's edge-to-edge flat panels.
  - Set image `contentScale` to `ContentScale.FillWidth` which displays the category graphics fully without any distortion or bounds cropping.

### B. Drag-to-Scroll vs Tap-to-Click Conflict
- **Problem**: On Desktop and Web, mouse dragging inside the pager was intercepted or triggered click events immediately on releasing the mouse click, launching the category detail view by mistake. Standard tap gesture detectors consumed pointer events, which disabled mouse scrolling entirely on Web.
- **Solution**: 
  - Implemented a custom event loop using `Modifier.pointerInput(Unit) { awaitPointerEventScope { ... } }`.
  - By **not** calling `consume()` on drag movements, we allow the drag events to propagate to the parent `HorizontalPager` so it can handle mouse dragging and scroll left/right freely.
  - The loop tracks pointer coordinate changes. If the pointer moves more than 15 pixels, the gesture is categorized as a drag (`isTap = false`), canceling the tap action and popping the card's press state back to normal size.
  - Clicks are only triggered if the pointer is released without significant drag movement.
  - Added micro-animation tactile feedback: when a user clicks/holds a card, it dynamically scales down to `98%` size, snapping back to normal when released or dragged away.

