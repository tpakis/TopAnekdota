package aithanasakis.anekdota

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformScrollbar(
    state: LazyListState,
    modifier: Modifier = Modifier
)
