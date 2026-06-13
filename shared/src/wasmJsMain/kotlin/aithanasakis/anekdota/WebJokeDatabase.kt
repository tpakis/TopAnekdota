package aithanasakis.anekdota

import kotlinx.browser.localStorage

class WebJokeDatabase : JokeDatabase {
    private val jokes = mutableListOf<Joke>()
    private var nextId = 1
    
    private fun getFavoriteTexts(): MutableSet<String> {
        val favsJson = localStorage.getItem("favorites_json") ?: ""
        if (favsJson.isEmpty()) return mutableSetOf()
        return favsJson.split("|||").filter { it.isNotEmpty() }.toMutableSet()
    }
    
    private fun saveFavoriteTexts(favs: Set<String>) {
        localStorage.setItem("favorites_json", favs.joinToString("|||"))
    }
    
    private fun getCustomJokes(): List<String> {
        val customJson = localStorage.getItem("custom_jokes_json") ?: ""
        if (customJson.isEmpty()) return emptyList()
        return customJson.split("|||").filter { it.isNotEmpty() }
    }
    
    private fun saveCustomJokes(customs: List<String>) {
        localStorage.setItem("custom_jokes_json", customs.joinToString("|||"))
    }

    override suspend fun insertJoke(category: String, text: String, isFavorite: Boolean, isCustom: Boolean): Int {
        val id = nextId++
        val joke = Joke(id, category, text, isFavorite, isCustom)
        jokes.add(joke)
        if (isCustom) {
            val customs = getCustomJokes().toMutableList()
            customs.add(text)
            saveCustomJokes(customs)
        }
        if (isFavorite) {
            val favs = getFavoriteTexts()
            favs.add(text)
            saveFavoriteTexts(favs)
        }
        return id
    }

    override suspend fun getJokes(category: String): List<Joke> {
        if (category == "agapimena") {
            return getFavorites()
        }
        val favs = getFavoriteTexts()
        return jokes.filter { it.category == category }.map {
            it.copy(isFavorite = favs.contains(it.text))
        }
    }

    override suspend fun setFavorite(jokeId: Int, isFavorite: Boolean) {
        val joke = jokes.find { it.id == jokeId } ?: return
        val favs = getFavoriteTexts()
        if (isFavorite) {
            favs.add(joke.text)
        } else {
            favs.remove(joke.text)
        }
        saveFavoriteTexts(favs)
    }

    override suspend fun getFavorites(): List<Joke> {
        val favs = getFavoriteTexts()
        val favoriteJokes = jokes.filter { favs.contains(it.text) }.map {
            it.copy(isFavorite = true)
        }.toMutableList()
        
        val existingTexts = favoriteJokes.map { it.text }.toSet()
        favs.forEach { text ->
            if (!existingTexts.contains(text)) {
                val isCustom = getCustomJokes().contains(text)
                val id = nextId++
                val dummyJoke = Joke(id, "agapimena", text, true, isCustom)
                favoriteJokes.add(dummyJoke)
            }
        }
        return favoriteJokes
    }

    override suspend fun searchJokes(category: String, query: String): List<Joke> {
        val jokesList = getJokes(category)
        return jokesList.filter { it.text.contains(query, ignoreCase = true) }
    }

    override suspend fun isEmpty(): Boolean {
        return jokes.filter { !it.isCustom }.isEmpty()
    }
}

actual fun createJokeDatabase(platformContext: Any?): JokeDatabase {
    return WebJokeDatabase()
}
