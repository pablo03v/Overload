package cloud.pablos.overload.ui.tabs.calendar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.views.TextView
import cloud.pablos.overload.ui.views.parseToLocalDateTime
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTabTopAppBar(
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val yearDialogState = remember { mutableStateOf(false) }

    val firstYear =
        if (itemState.items.isEmpty()) {
            LocalDate.now().year
        } else {
            itemState.items.minByOrNull { it.startTime }?.let { parseToLocalDateTime(it.startTime).year } ?: LocalDate.now().year
        }
    val yearsCount = LocalDate.now().year - firstYear

    Surface(
        tonalElevation = NavigationBarDefaults.Elevation,
        color = MaterialTheme.colorScheme.background,
    ) {
        TopAppBar(
            title = {
                TextView(
                    text = stringResource(id = R.string.calendar),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            },
            actions = {
                if (yearsCount > 0) {
                    Button(
                        onClick = { yearDialogState.value = true },
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        TextView(itemState.selectedYearCalendar.toString())
                    }
                    if (yearDialogState.value) {
                        CalendarTabYearDialog(
                            firstYear = firstYear,
                            itemEvent = itemEvent,
                            onClose = { yearDialogState.value = false },
                        )
                    }
                }
            },
        )
    }
}
