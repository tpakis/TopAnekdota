---
name: b4a-to-kmp-migration
description: >-
  Architectural guide, gotchas, and best practices for migrating legacy 
  Basic4Android (B4A) applications to modern Kotlin Multiplatform (KMP) 
  projects targeting Android, iOS, Desktop, and Web.
---

# B4A to KMP Migration Guide

## Overview
This skill outlines the step-by-step best practices, architecture mappings, code conversions, and common gotchas when migrating legacy Basic4Android (B4A) applications to Compose-based Kotlin Multiplatform (KMP).

---

## Architectural Mapping (B4A vs. KMP)

| B4A Architecture / API | KMP Multiplatform Equivalent | Description / Mapping Strategy |
| :--- | :--- | :--- |
| **Activity / Visual Designer Layouts (`.bal`)** | **Compose Multiplatform (`@Composable`)** | Replace imperative layout loads with declarative Compose UI trees. |
| **`Process_Globals` / `Globals`** | **Standard Kotlin Classes & Compose State** | Map global variables to singletons, view models, or Compose state variables (`mutableStateOf`). |
| **`KeyValueStore` (KVS) Library** | **`KeyValueStorage` (Expect/Actual Interface)** | Wrap platform-native preferences (Android `SharedPreferences`, iOS `NSUserDefaults`, JVM `Preferences`, Web `localStorage`). |
| **`SQL` Object / Cursor** | **`SQLiteConnection` & `SQLiteStatement`** | Use `androidx.sqlite:sqlite-bundled` for native driver access across Android, iOS, and Desktop. |
| **`File.DirAssets` / Files Folder** | **`composeResources/files/`** | Place files in the common resource distribution and read them via `Res.readBytes("files/name")`. |
| **HTML Utilities (`String.Replace`)** | **Kotlin String Replacement Functions** | Map B4A HTML-formatted strings (CSV exports) to sanitized Kotlin strings. |

---

## Code Translations & Implementation Patterns

### 1. File Access & Assets
In B4A, files are loaded synchronously from the assets directory:
```basic
' B4A Code
Dim TextReader1 As TextReader
TextReader1.Initialize(File.OpenInput(File.DirAssets, "diafora.txt"))
Dim line As String = TextReader1.ReadLine
```
In KMP, reading resources is asynchronous and platform-independent using Compose Resources:
```kotlin
// KMP Kotlin Code
val bytes = Res.readBytes("files/diafora.txt")
val content = bytes.decodeToString()
val lines = content.split(Regex("\\r?\\n"))
```

### 2. Local Key-Value Preferences
In B4A, basic preferences are stored in the custom `KeyValueStore` class:
```basic
' B4A Code
Dim kvs As KeyValueStore
kvs.Initialize(File.DirInternal, "settings")
kvs.PutSimple("fontSize", 16)
Dim size As Int = kvs.GetSimple("fontSize")
```
In KMP, we define a common interface and implement it on each platform:
```kotlin
// commonMain Interface
interface KeyValueStorage {
    fun getInt(key: String, defaultValue: Int): Int
    fun putInt(key: String, value: Int)
}
```
**Platform Implementations**:
- **Android**: `context.getSharedPreferences("prefs", Context.MODE_PRIVATE)`
- **iOS**: `NSUserDefaults.standardUserDefaults`
- **JVM (Desktop)**: `Preferences.userRoot().node("settings")`
- **Web (JS/WasmJs)**: `window.localStorage`

### 3. SQLite Database Operations
In B4A, transactions and executions are managed through the B4A SQL library:
```basic
' B4A Code
Dim SQL1 As SQL
SQL1.Initialize(File.DirInternal, "jokes.db", True)
SQL1.BeginTransaction
Try
    SQL1.ExecNonQuery2("INSERT INTO jokes (category, text) VALUES (?, ?)", Array As Object("diafora", "Joke text"))
    SQL1.TransactionSuccessful
Catch
    Log(LastException.Message)
End Try
SQL1.EndTransaction
```
In KMP, we define an abstract database interface and use the `androidx.sqlite` bundled driver for SQLite platforms:
```kotlin
// KMP Kotlin Code
class SqliteJokeDatabase(private val dbPath: String) : JokeDatabase {
    private val driver: SQLiteDriver = BundledSQLiteDriver()
    private var connection: SQLiteConnection? = null

    private fun getConnection(): SQLiteConnection {
        if (connection == null) {
            connection = driver.open(dbPath)
            connection!!.prepare("CREATE TABLE IF NOT EXISTS jokes (...)").use { it.step() }
        }
        return connection!!
    }

    override suspend fun beginTransaction() {
        getConnection().prepare("BEGIN IMMEDIATE TRANSACTION").use { it.step() }
    }

    override suspend fun commitTransaction() {
        getConnection().prepare("COMMIT").use { it.step() }
    }

    override suspend fun rollbackTransaction() {
        getConnection().prepare("ROLLBACK").use { it.step() }
    }

    override suspend fun insertJoke(category: String, text: String): Int {
        val conn = getConnection()
        conn.prepare("INSERT INTO jokes (category, text) VALUES (?, ?)").use { stmt ->
            stmt.bindText(1, category)
            stmt.bindText(2, text)
            stmt.step()
        }
    }
}
```

---

## Critical Gotchas & Best Practices

### 1. Database Prepopulation & safe Upgrades
- **Problem**: Overwriting the SQLite database file on app updates erases user favorites or custom jokes.
- **Best Practice**:
  - Keep standard static records in clean text or CSV resource files (e.g. inside `composeResources/files/`).
  - Read resource files and populate the database programmatically on first launch.
  - Track a `dbVersion` preference in settings. When new records are imported:
    1. Read and backup the text of all standard jokes currently marked as favorites (`is_favorite = 1` and `is_custom = 0`).
    2. Clear only standard jokes (`DELETE FROM jokes WHERE is_custom = 0`). This preserves custom jokes.
    3. Re-populate database from the new resource files.
    4. Match and re-apply favorites state from the backup set.
    5. Update `dbVersion` in settings.

### 2. SQLite Bulk Insertion Speed
- **Problem**: Inserting 5,000 jokes without a transaction causes SQLite to commit and sync to the disk 5,000 times, causing application freezing of up to 60+ seconds.
- **Best Practice**: Wrap the entire populating loop in a single transaction (`BEGIN IMMEDIATE TRANSACTION` ... `COMMIT`). This combines all insertions into a single transaction and drops populate time from a minute to **under 100 milliseconds**.

### 3. Pager & List Scrolling Optimizations
- **Problem**: 
  1. Compose list containers (`LazyColumn`) inside unconstrained layouts (e.g. `AnimatedContent` lacking `.fillMaxSize()`) fail to establish scroll boundaries and attempt to measure/render all items at once.
  2. Infinite carousels (`HorizontalPager`) natively stop at bounds.
- **Best Practice**:
  - Explicitly restrict list heights (e.g., `weight(1f)` or `fillMaxSize()`).
  - Map pagers to wrap infinitely by initializing `pagerState` at the middle of a very large range (`10,000 * size`) and retrieving items modulo-style: `val item = items[page % items.size]`.
