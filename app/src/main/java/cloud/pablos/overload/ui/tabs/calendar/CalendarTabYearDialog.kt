package cloud.pablos.overload.ui.tabs.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarViewDay
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cloud.pablos.overload.R
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.ui.views.TextView
import java.time.LocalDate

@Composable
fun CalendarTabYearDialog(
    firstYear: Int,
    itemEvent: (ItemEvent) -> Unit,
    onClose: () -> Unit,
) {
    Dialog(
        onDismissRequest = onClose,
        content = {
            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = NavigationBarDefaults.Elevation,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth(),
            ) {
                YearDialogContent(firstYear = firstYear, itemEvent = itemEvent, onClose = onClose)
            }
        },
    )
}

@Composable
private fun YearDialogContent(
    firstYear: Int,
    itemEvent: (ItemEvent) -> Unit,
    onClose: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.CalendarViewDay,
            contentDescription = stringResource(id = R.string.select_year),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp),
        )

        TextView(
            text = stringResource(id = R.string.select_year),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )

        YearListContent(firstYear = firstYear, itemEvent = itemEvent, onClose = onClose)
    }
}

@Composable
private fun YearListContent(
    firstYear: Int,
    itemEvent: (ItemEvent) -> Unit,
    onClose: () -> Unit,
) {
    LazyColumn {
        val currentYear = LocalDate.now().year
        items((currentYear downTo firstYear).toList()) { year ->
            YearRow(year = year, itemEvent = itemEvent, onClose = onClose)
            if (year != firstYear) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun YearRow(
    year: Int,
    itemEvent: (ItemEvent) -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    itemEvent(ItemEvent.SetSelectedYearCalendar(year))
                    onClose()
                }
                .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        TextView(text = year.toString())
    }
}
