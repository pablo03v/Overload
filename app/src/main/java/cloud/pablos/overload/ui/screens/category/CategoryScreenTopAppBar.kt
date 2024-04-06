package cloud.pablos.overload.ui.screens.day

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.tabs.home.getFormattedDate
import cloud.pablos.overload.ui.views.TextView
import cloud.pablos.overload.ui.views.getLocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreenTopAppBar(state: ItemState) {
    val selectedDay = getLocalDate(state.selectedDayCalendar)
    val title = getFormattedDate(selectedDay, true)

    Surface(
        tonalElevation = NavigationBarDefaults.Elevation,
        color = MaterialTheme.colorScheme.background,
    ) {
        TopAppBar(
            title = {
                TextView(
                    text = title,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            },
        )
    }
}
