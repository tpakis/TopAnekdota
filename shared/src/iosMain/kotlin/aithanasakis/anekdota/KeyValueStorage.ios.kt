package aithanasakis.anekdota

import platform.Foundation.NSUserDefaults

class IosKeyValueStorage : KeyValueStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getString(key: String, defaultValue: String): String {
        return defaults.stringForKey(key) ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        defaults.setObject(value, key)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        val value = defaults.objectForKey(key)
        return (value as? Long)?.toInt() ?: defaultValue
    }

    override fun putInt(key: String, value: Int) {
        defaults.setInteger(value.toLong(), key)
    }
}

actual fun createKeyValueStorage(context: Any?): KeyValueStorage {
    return IosKeyValueStorage()
}
