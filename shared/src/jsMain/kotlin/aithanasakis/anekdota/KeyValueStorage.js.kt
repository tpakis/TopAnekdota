package aithanasakis.anekdota

import kotlinx.browser.localStorage

class JsKeyValueStorage : KeyValueStorage {
    override fun getString(key: String, defaultValue: String): String {
        return localStorage.getItem(key) ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        localStorage.setItem(key, value)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return localStorage.getItem(key)?.toIntOrNull() ?: defaultValue
    }

    override fun putInt(key: String, value: Int) {
        localStorage.setItem(key, value.toString())
    }
}

actual fun createKeyValueStorage(context: Any?): KeyValueStorage {
    return JsKeyValueStorage()
}
