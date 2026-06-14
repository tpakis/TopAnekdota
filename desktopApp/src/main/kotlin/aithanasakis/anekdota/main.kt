package aithanasakis.anekdota

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import topanekdota.shared.generated.resources.Res
import topanekdota.shared.generated.resources.app_icon

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TopAnekdota",
        icon = painterResource(Res.drawable.app_icon),
        state = rememberWindowState(
            size = DpSize(628.dp, 840.dp)
        )
    ) {
        App()
    }
}