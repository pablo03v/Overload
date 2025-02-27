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
fun DayScreenTopAppBar(itemState: ItemState) {
    val selectedDay = getLocalDate(itemState.selectedDayCalendar)
    val title = getFormattedDate(selectedDay, true)

    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = NavigationBarDefaults.Elevation,
    ) {
        TopAppBar(
            {
                TextView(
                    title,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            },
        )
    }
}
