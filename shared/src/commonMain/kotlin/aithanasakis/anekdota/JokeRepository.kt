package aithanasakis.anekdota

import org.jetbrains.compose.resources.ExperimentalResourceApi
import topanekdota.shared.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JokeRepository(private val database: JokeDatabase) {

    suspend fun ensureDatabasePopulated(settingsManager: SettingsManager) {
        val targetVersion = 2
        val dbIsEmpty = database.isEmpty()
        val savedVersion = settingsManager.jokesVersion

        if (!dbIsEmpty && savedVersion >= targetVersion) return

        val files = listOf(
            "diafora" to "diafora.txt",
            "ksanthies" to "ksanthies.txt",
            "totos" to "totos.txt",
            "pontiaka" to "pontiaka.txt",
            "ponira" to "ponira.txt",
            "annoula" to "annoula.txt",
            "chuck" to "chuck.txt",
            "sintoma" to "sintoma.txt",
            "mikres" to "mikres.txt",
            "zodia" to "zodia.txt"
        )

        withContext(ioDispatcher) {
            val favoriteTexts = if (!dbIsEmpty) {
                database.getFavorites().filter { !it.isCustom }.map { it.text }.toSet()
            } else {
                emptySet()
            }

            database.beginTransaction()
            try {
                if (!dbIsEmpty) {
                    database.clearStandardJokes()
                }

                for ((category, filename) in files) {
                    try {
                        val bytes = Res.readBytes("files/$filename")
                        val content = bytes.decodeToString()
                        val lines = content.split(Regex("\\r?\\n"))
                        for (line in lines) {
                            val cleaned = sanitizeJokeText(line)
                            if (cleaned.isNotEmpty()) {
                                val isFav = favoriteTexts.contains(cleaned)
                                database.insertJoke(category, cleaned, isFavorite = isFav, isCustom = false)
                            }
                        }
                    } catch (e: Exception) {
                        println("Error loading file files/$filename: ${e.message}")
                    }
                }
                database.commitTransaction()
            } catch (e: Exception) {
                database.rollbackTransaction()
                throw e
            }
            settingsManager.jokesVersion = targetVersion
        }
    }

    suspend fun getJokes(category: String): List<Joke> {
        if (category == "agapimena") {
            return database.getFavorites()
        }
        return database.getJokes(category)
    }

    suspend fun searchJokes(category: String, query: String): List<Joke> {
        if (category == "agapimena") {
            return database.getFavorites().filter { it.text.contains(query, ignoreCase = true) }
        }
        return database.searchJokes(category, query)
    }

    suspend fun getFavorites(): List<Joke> {
        return database.getFavorites()
    }

    suspend fun toggleFavorite(joke: Joke, isFavorite: Boolean) {
        database.setFavorite(joke.id, isFavorite)
    }

    suspend fun addCustomJoke(text: String): Joke {
        // Custom jokes are always added to the favorites ("agapimena") category as per B4A logic
        val cleaned = sanitizeJokeText(text)
        val id = database.insertJoke("agapimena", cleaned, isFavorite = true, isCustom = true)
        return Joke(id, "agapimena", cleaned, isFavorite = true, isCustom = true)
    }

    private fun sanitizeJokeText(text: String): String {
        var cleaned = text.trim()
        
        // Remove B4A-specific CSV delimiters and trailing semicolons/quotes
        while (cleaned.endsWith(";") || cleaned.endsWith("\"") || cleaned.endsWith("'")) {
            cleaned = cleaned.substring(0, cleaned.length - 1).trim()
        }
        while (cleaned.startsWith("\"") || cleaned.startsWith("'")) {
            cleaned = cleaned.substring(1).trim()
        }
        
        cleaned = cleaned
            .replace("<center>", "")
            .replace("</center>", "")
            .replace("<p>", "")
            .replace("</p>", "")
            .replace("<br />", "\n")
            .replace("<br/>", "\n")
            .replace("<br>", "\n")
            .replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
        
        return cleaned.trim()
    }
}
