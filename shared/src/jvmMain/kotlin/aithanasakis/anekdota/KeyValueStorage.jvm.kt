package aithanasakis.anekdota

import java.util.prefs.Preferences

class JvmKeyValueStorage : KeyValueStorage {
    private val prefs: Preferences = Preferences.userRoot().node("aithanasakis.anekdota.settings")

    override fun getString(key: String, defaultValue: String): String {
        return prefs.get(key, defaultValue)
    }

    override fun putString(key: String, value: String) {
        prefs.put(key, value)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    override fun putInt(key: String, value: Int) {
        prefs.putInt(key, value)
    }
}

actual fun createKeyValueStorage(context: Any?): KeyValueStorage {
    return JvmKeyValueStorage()
}
