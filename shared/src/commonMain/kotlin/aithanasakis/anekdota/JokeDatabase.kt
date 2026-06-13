package aithanasakis.anekdota

interface JokeDatabase {
    suspend fun insertJoke(category: String, text: String, isFavorite: Boolean, isCustom: Boolean): Int
    suspend fun getJokes(category: String): List<Joke>
    suspend fun setFavorite(jokeId: Int, isFavorite: Boolean)
    suspend fun getFavorites(): List<Joke>
    suspend fun searchJokes(category: String, query: String): List<Joke>
    suspend fun isEmpty(): Boolean
}

expect fun createJokeDatabase(platformContext: Any? = null): JokeDatabase
