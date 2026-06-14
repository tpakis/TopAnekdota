package aithanasakis.anekdota

import android.content.Context
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

import androidx.sqlite.SQLiteStatement

inline fun <R> SQLiteStatement.use(block: (SQLiteStatement) -> R): R {
    try {
        return block(this)
    } finally {
        close()
    }
}

class SqliteJokeDatabase(private val dbPath: String) : JokeDatabase {
    private val driver: SQLiteDriver = BundledSQLiteDriver()
    private var connection: SQLiteConnection? = null

    @Synchronized
    private fun getConnection(): SQLiteConnection {
        if (connection == null) {
            connection = driver.open(dbPath)
            createTable()
        }
        return connection!!
    }

    private fun createTable() {
        val conn = connection!!
        conn.prepare("CREATE TABLE IF NOT EXISTS jokes (id INTEGER PRIMARY KEY AUTOINCREMENT, category TEXT, text TEXT, is_favorite INTEGER, is_custom INTEGER)").use { stmt ->
            stmt.step()
        }
    }

    override suspend fun insertJoke(category: String, text: String, isFavorite: Boolean, isCustom: Boolean): Int {
        val conn = getConnection()
        conn.prepare("INSERT INTO jokes (category, text, is_favorite, is_custom) VALUES (?, ?, ?, ?)").use { stmt ->
            stmt.bindText(1, category)
            stmt.bindText(2, text)
            stmt.bindInt(3, if (isFavorite) 1 else 0)
            stmt.bindInt(4, if (isCustom) 1 else 0)
            stmt.step()
        }
        var lastId = 0
        conn.prepare("SELECT last_insert_rowid()").use { stmt ->
            if (stmt.step()) {
                lastId = stmt.getInt(0)
            }
        }
        return lastId
    }

    override suspend fun getJokes(category: String): List<Joke> {
        val conn = getConnection()
        val jokes = mutableListOf<Joke>()
        conn.prepare("SELECT id, category, text, is_favorite, is_custom FROM jokes WHERE category = ?").use { stmt ->
            stmt.bindText(1, category)
            while (stmt.step()) {
                jokes.add(Joke(
                    id = stmt.getInt(0),
                    category = stmt.getText(1),
                    text = stmt.getText(2),
                    isFavorite = stmt.getInt(3) == 1,
                    isCustom = stmt.getInt(4) == 1
                ))
            }
        }
        return jokes
    }

    override suspend fun setFavorite(jokeId: Int, isFavorite: Boolean) {
        val conn = getConnection()
        conn.prepare("UPDATE jokes SET is_favorite = ? WHERE id = ?").use { stmt ->
            stmt.bindInt(1, if (isFavorite) 1 else 0)
            stmt.bindInt(2, jokeId)
            stmt.step()
        }
    }

    override suspend fun getFavorites(): List<Joke> {
        val conn = getConnection()
        val jokes = mutableListOf<Joke>()
        conn.prepare("SELECT id, category, text, is_favorite, is_custom FROM jokes WHERE is_favorite = 1").use { stmt ->
            while (stmt.step()) {
                jokes.add(Joke(
                    id = stmt.getInt(0),
                    category = stmt.getText(1),
                    text = stmt.getText(2),
                    isFavorite = true,
                    isCustom = stmt.getInt(4) == 1
                ))
            }
        }
        return jokes
    }

    override suspend fun searchJokes(category: String, query: String): List<Joke> {
        val conn = getConnection()
        val jokes = mutableListOf<Joke>()
        conn.prepare("SELECT id, category, text, is_favorite, is_custom FROM jokes WHERE category = ? AND text LIKE ?").use { stmt ->
            stmt.bindText(1, category)
            stmt.bindText(2, "%$query%")
            while (stmt.step()) {
                jokes.add(Joke(
                    id = stmt.getInt(0),
                    category = stmt.getText(1),
                    text = stmt.getText(2),
                    isFavorite = stmt.getInt(3) == 1,
                    isCustom = stmt.getInt(4) == 1
                ))
            }
        }
        return jokes
    }

    override suspend fun clearStandardJokes() {
        val conn = getConnection()
        conn.prepare("DELETE FROM jokes WHERE is_custom = 0").use { stmt ->
            stmt.step()
        }
    }

    override suspend fun beginTransaction() {
        val conn = getConnection()
        conn.prepare("BEGIN IMMEDIATE TRANSACTION").use { stmt ->
            stmt.step()
        }
    }

    override suspend fun commitTransaction() {
        val conn = getConnection()
        conn.prepare("COMMIT").use { stmt ->
            stmt.step()
        }
    }

    override suspend fun rollbackTransaction() {
        val conn = getConnection()
        conn.prepare("ROLLBACK").use { stmt ->
            stmt.step()
        }
    }

    override suspend fun isEmpty(): Boolean {
        val conn = getConnection()
        var count = 0
        conn.prepare("SELECT count(*) FROM jokes").use { stmt ->
            if (stmt.step()) {
                count = stmt.getInt(0)
            }
        }
        return count == 0
    }
}

actual fun createJokeDatabase(platformContext: Any?): JokeDatabase {
    val context = platformContext as? Context ?: throw IllegalArgumentException("Android Context required")
    val dbFile = context.getDatabasePath("jokes.db")
    // Ensure parent directory exists
    dbFile.parentFile?.mkdirs()
    return SqliteJokeDatabase(dbFile.absolutePath)
}
