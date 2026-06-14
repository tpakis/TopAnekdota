package aithanasakis.anekdota

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformScrollbar(
    state: LazyListState,
    modifier: Modifier
) {
    // No-op for Android as touch scrolling is native
}
