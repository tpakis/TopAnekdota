package aithanasakis.anekdota

interface KeyValueStorage {
    fun getString(key: String, defaultValue: String): String
    fun putString(key: String, value: String)
    fun getInt(key: String, defaultValue: Int): Int
    fun putInt(key: String, value: Int)
}

expect fun createKeyValueStorage(context: Any? = null): KeyValueStorage
