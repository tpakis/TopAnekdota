package aithanasakis.anekdota

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform