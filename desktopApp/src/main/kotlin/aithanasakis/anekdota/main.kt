package aithanasakis.anekdota

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TopAnekdota",
        state = rememberWindowState(
            size = DpSize(628.dp, 840.dp)
        )
    ) {
        App()
    }
}