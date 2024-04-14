package cloud.pablos.overload.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.data.Converters
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.tabs.calendar.CalendarTabYearDialog
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ChangeYearButton(
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val yearDialogState = remember { mutableStateOf(false) }

    val firstYear =
        if (itemState.items.isEmpty()) {
            LocalDate.now().year
        } else {
            itemState.items.minByOrNull { it.startTime }?.let {
                Converters.convertStringToLocalDateTime(
                    it.startTime,
                ).year
            } ?: LocalDate.now().year
        }

    val yearsCount = LocalDate.now().year - firstYear

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
}
