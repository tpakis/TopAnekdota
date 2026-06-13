package aithanasakis.anekdota

import org.jetbrains.compose.resources.ExperimentalResourceApi
import topanekdota.shared.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JokeRepository(private val database: JokeDatabase) {

    suspend fun ensureDatabasePopulated() {
        if (!database.isEmpty()) return

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
            for ((category, filename) in files) {
                try {
                    val bytes = Res.readBytes("files/$filename")
                    val content = bytes.decodeToString()
                    // B4A files use standard newlines, but let's split by both \r\n and \n
                    val lines = content.split(Regex("\\r?\\n"))
                    for (line in lines) {
                        val cleaned = sanitizeJokeText(line)
                        if (cleaned.isNotEmpty()) {
                            database.insertJoke(category, cleaned, isFavorite = false, isCustom = false)
                        }
                    }
                } catch (e: Exception) {
                    println("Error loading file files/$filename: ${e.message}")
                }
            }
        }
    }

    suspend fun getJokes(category: String): List<Joke> {
        return database.getJokes(category)
    }

    suspend fun searchJokes(category: String, query: String): List<Joke> {
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
