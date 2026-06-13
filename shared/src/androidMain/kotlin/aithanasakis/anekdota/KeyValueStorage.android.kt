package aithanasakis.anekdota

import android.content.Context
import android.content.SharedPreferences

class AndroidKeyValueStorage(context: Context) : KeyValueStorage {
    private val prefs: SharedPreferences = context.getSharedPreferences("top_anekdota_settings", Context.MODE_PRIVATE)

    override fun getString(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    override fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }
}

actual fun createKeyValueStorage(context: Any?): KeyValueStorage {
    val androidContext = context as? Context ?: throw IllegalArgumentException("Android context required")
    return AndroidKeyValueStorage(androidContext)
}
