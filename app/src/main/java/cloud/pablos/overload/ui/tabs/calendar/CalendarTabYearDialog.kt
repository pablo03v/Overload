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
        onClose,
        content = {
            Surface(
                Modifier.fillMaxWidth(),
                MaterialTheme.shapes.large,
                MaterialTheme.colorScheme.background,
                tonalElevation = NavigationBarDefaults.Elevation,
            ) {
                YearDialogContent(firstYear, itemEvent, onClose)
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
        Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Rounded.CalendarViewDay,
            stringResource(R.string.select_year),
            Modifier.padding(16.dp),
            MaterialTheme.colorScheme.primary,
        )

        TextView(
            stringResource(R.string.select_year),
            Modifier.padding(top = 16.dp, bottom = 8.dp),
            MaterialTheme.typography.titleLarge.fontSize,
        )

        YearListContent(firstYear, itemEvent, onClose)
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
            YearRow(year, itemEvent, onClose)
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
        Modifier
            .fillMaxWidth()
            .clickable {
                itemEvent(ItemEvent.SetSelectedYearCalendar(year))
                onClose()
            }
            .padding(16.dp),
        Arrangement.Center,
    ) {
        TextView(year.toString())
    }
}
