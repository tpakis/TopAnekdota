---
name: b4a-to-kmp-migration
description: >-
  Architectural guide, gotchas, and best practices for migrating legacy 
  Basic4Android (B4A) applications to modern Kotlin Multiplatform (KMP) 
  projects targeting Android, iOS, Desktop, and Web.
---

# B4A to KMP Migration Guide

## Overview
This skill outlines the step-by-step best practices, common gotchas, and optimizations required when migrating legacy Basic4Android (B4A) codebases to Compose-based Kotlin Multiplatform (KMP).

## Key Gotchas & Best Practices

### 1. Database Prepopulation & Updates
- **Problem**: Legacy B4A apps often store static databases directly on the device. In KMP, shipping a raw binary `.db` file in resource assets is problematic because a database must be writeable, forcing platform-specific file copy logic. Overwriting the `.db` file on app update deletes user favorites or custom jokes.
- **Best Practice**:
  - Keep standard static records in clean text or CSV resource files (e.g. inside `composeResources/files/`).
  - Read resource files and populate the database programmatically on first launch.
  - Implement a `jokesVersion` (or `dbVersion`) key in local settings.
  - When updating jokes in future versions:
    1. Check if the stored settings version is less than the new version.
    2. Retrieve any user favorites or custom entries from the current DB.
    3. Clear standard static records only (e.g. `DELETE FROM jokes WHERE is_custom = 0`).
    4. Repopulate all records from the new resource files.
    5. Re-apply favorites matching the saved entries by text signature.
    6. Update the settings version key.

### 2. SQLite Bulk Insertion Performance
- **Problem**: Inserting thousands of records into SQLite one-by-one commits a new transaction for each insertion. On mobile flash memory, this triggers thousands of disk-sync writes, taking up to a minute and freezing the application start-up.
- **Best Practice**:
  - Expose database transaction control methods (`beginTransaction()`, `commitTransaction()`, `rollbackTransaction()`) in the common database interface.
  - Execute SQL controls manually for SQLite platforms:
    - `BEGIN IMMEDIATE TRANSACTION`
    - `COMMIT`
    - `ROLLBACK`
  - Wrap the entire resource parsing and insertion loop inside a single transaction. This reduces commit operations to **1 single write**, speeding up the process from 60 seconds to **under 100 milliseconds** (instant loading).

### 3. Desktop and Web Paging & Scrolling
- **Problem**: Compose Multiplatform's `LazyColumn` scrolling behaves differently across targets. 
  1. If outer containers (like `AnimatedContent`) lack explicit size limits (e.g. `Modifier.fillMaxSize()`), they propagate infinite height constraints to child lists. This causes `LazyColumn` to render all list items at once, breaking scrollable viewports.
  2. Native mouse-drag list scrolling (press left click + drag list up and down) is not natively enabled on desktop/web, leading to frozen user interactions when trying to drag.
- **Best Practice**:
  - Ensure the parent container of `LazyColumn` has bounded dimensions (e.g. `.fillMaxSize()` or `.weight(1f)`).
  - Implement custom pointer inputs on Desktop/Web to intercept mouse drags in `PointerEventPass.Initial`, scroll the list using `listState.dispatchRawDelta(-deltaY)`, and apply deceleration decay momentum upon mouse release using a `VelocityTracker` and `ScrollableDefaults.flingBehavior()`.

### 4. Backward-Compatible API Methods
- **Problem**: Compiling KMP with Kotlin/JVM and Android desugaring might result in `NoSuchMethodError` crashes at runtime on older Android devices when using newer JDK methods.
- **Example**: `SnapshotStateList.removeLast()` compiles to a JDK 21 method not available on older Android runtimes.
- **Best Practice**: Use fully backward-compatible JVM methods, such as `list.removeAt(list.lastIndex)`.

### 5. Infinite Wrapping Carousel
- **Problem**: A standard `HorizontalPager` limits swipe navigation from page `0` to the last page.
- **Best Practice**:
  - Set the pager `pageCount` to a very large virtual number (e.g., `10000 * categories.size`).
  - Initialize the state at the exact middle of the virtual range `pageCount / 2`.
  - Map absolute page indexes to category indexes using modulo math (`page % categories.size`) for pager cards, pager indicators, selected clicks, and key navigations.
  - This enables infinite wrapping swipes in both directions.

---

## Migration Workflow

### Step 1: Resource Distillation
1. Convert B4A databases or CSV exports to clean, line-delimited text assets.
2. Standardize all HTML/B4A entity characters (e.g. replacing `<br/>` with `\n` and converting HTML code entities).

### Step 2: Interface Mapping
1. Define abstract `KeyValueStorage` and `JokeDatabase` interfaces in `commonMain`.
2. Implement actual platform behaviors:
   - Android (`SharedPreferences` & SQLite)
   - iOS (`NSUserDefaults` & SQLite)
   - JVM (`Preferences` & SQLite)
   - JS/Wasm (`localStorage` & in-memory arrays)

### Step 3: Transaction Optimization
Wrap database initialization and prepopulation logic in transaction boundaries.

### Step 4: UI Alignment
Adjust layouts, scrolling behavior, keyboard arrow controls, and gesture decoders for a seamless desktop/web/mobile experience.
