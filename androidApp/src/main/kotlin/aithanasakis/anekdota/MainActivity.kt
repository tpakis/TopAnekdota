package aithanasakis.anekdota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Set status bar color to the theme's nice blue and make icons light (white)
        window.statusBarColor = android.graphics.Color.parseColor("#0F60A8")
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            App(this)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(null)
}