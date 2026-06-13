package aithanasakis.anekdota

data class Joke(
    val id: Int,
    val category: String,
    val text: String,
    val isFavorite: Boolean,
    val isCustom: Boolean
)
