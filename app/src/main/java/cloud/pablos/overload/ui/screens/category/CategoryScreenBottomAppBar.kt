package cloud.pablos.overload.ui.screens.day

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun CategoryScreenBottomAppBar(onNavigate: () -> Unit) {
    BottomAppBar(
        actions = {
            IconButton(onClick = { onNavigate() }) {
                Icon(
                    Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = "Go Back",
                )
            }
        },
    )
}
