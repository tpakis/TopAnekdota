package aithanasakis.anekdota

class SettingsManager(private val storage: KeyValueStorage) {

    var fontSize: Int
        get() = storage.getInt("size", 16)
        set(value) = storage.putInt("size", value)

    var fontIndex: Int
        get() = storage.getInt("font", 0)
        set(value) = storage.putInt("font", value)

    var fontName: String
        get() = storage.getString("fontname", "Default")
        set(value) = storage.putString("fontname", value)

    // B4A color integers (AARRGGBB in signed 32-bit int)
    // In signed int: -16777216 is Black, -1 is White.
    // In KMP we will convert them to Long/ULong for Compose.
    var fontColor: Long
        get() = storage.getString("fontcolor_l", "4278190080").toLong() // 0xFF000000 (Black)
        set(value) = storage.putString("fontcolor_l", value.toString())

    var backgroundColor: Long
        get() = storage.getString("backcolor_l", "4294967295").toLong() // 0xFFFFFFFF (White)
        set(value) = storage.putString("backcolor_l", value.toString())
}
