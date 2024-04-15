package cloud.pablos.overload.ui.tabs.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.Converters.Companion.convertStringToLocalDateTime
import cloud.pablos.overload.data.Helpers.Companion.getItems
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.views.TextView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun HomeTabManualDialog(
    onClose: () -> Unit,
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val context = LocalContext.current

    val date = LocalDate.now()
    val dateTime = LocalDateTime.now()

    var selectedStart: LocalDateTime
    var selectedEnd = dateTime
    var selectedPause by remember { mutableStateOf(false) }

    var selectedStartDateText by remember { mutableStateOf("") }
    var selectedStartTimeText by remember { mutableStateOf("") }
    var selectedEndDateText by remember { mutableStateOf("") }
    var selectedEndTimeText by remember { mutableStateOf("") }

    val itemsForToday = getItems(categoryState, itemState, date)

    selectedStart =
        if (itemsForToday.isNotEmpty() && itemsForToday.last().endTime.isNotBlank()) {
            convertStringToLocalDateTime(itemsForToday.last().endTime)
        } else {
            dateTime
        }
    selectedStartDateText = getFormattedDate(selectedStart.toLocalDate(), true)
    selectedStartTimeText = getFormattedTime(selectedStart)

    selectedEndDateText = getFormattedDate(selectedEnd.toLocalDate(), true)
    selectedEndTimeText = getFormattedTime(selectedEnd)

    selectedPause =
        if (itemsForToday.isNotEmpty()) {
            itemsForToday.last().pause.not()
        } else {
            false
        }

    val calendar = Calendar.getInstance()

    fun updateSelectedTime(
        newDateTime: LocalDateTime,
        isStart: Boolean,
    ) {
        if (isStart) {
            if (newDateTime.toLocalDate() <= date) {
                selectedStart = newDateTime
                selectedStartDateText = getFormattedDate(selectedStart.toLocalDate(), true)
                selectedStartTimeText = getFormattedTime(selectedStart)

                if (selectedStart > selectedEnd) {
                    selectedEnd = selectedStart
                    selectedEndDateText = getFormattedDate(selectedEnd.toLocalDate(), true)
                    selectedEndTimeText = getFormattedTime(selectedEnd)
                }
            }
        } else {
            selectedEnd = newDateTime
            selectedEndDateText = getFormattedDate(selectedEnd.toLocalDate(), true)
            selectedEndTimeText = getFormattedTime(selectedEnd)

            if (selectedEnd < selectedStart) {
                selectedStart = selectedEnd
                selectedStartDateText = getFormattedDate(selectedStart.toLocalDate(), true)
                selectedStartTimeText = getFormattedTime(selectedStart)
            }
        }
    }

    val startDatePicker =
        DatePickerDialog(
            context,
            { _, year: Int, month: Int, day: Int ->
                val calcMonth = month + 1
                val newStartDateTime =
                    selectedStart.withYear(year).withMonth(calcMonth).withDayOfMonth(day)

                updateSelectedTime(newStartDateTime, true)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        )

    val startTimePicker =
        TimePickerDialog(
            context,
            { _, selectedHour: Int, selectedMinute: Int ->
                val newStartDateTime = selectedStart.withHour(selectedHour).withMinute(selectedMinute)

                updateSelectedTime(newStartDateTime, true)
            },
            selectedStart.hour,
            selectedStart.minute,
            false,
        )

    val endDatePicker =
        DatePickerDialog(
            context,
            { _, year: Int, month: Int, day: Int ->
                val calcMonth = month + 1
                val newEndDateTime = selectedEnd.withYear(year).withMonth(calcMonth).withDayOfMonth(day)

                updateSelectedTime(newEndDateTime, false)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        )

    val endTimePicker =
        TimePickerDialog(
            context,
            { _, selectedHour: Int, selectedMinute: Int ->
                val newEndDateTime = selectedEnd.withHour(selectedHour).withMinute(selectedMinute)

                updateSelectedTime(newEndDateTime, false)
            },
            selectedEnd.hour,
            selectedEnd.minute,
            false,
        )

    AlertDialog(
        onDismissRequest = onClose,
        icon = {
            Icon(
                imageVector = Icons.Rounded.AddCircle,
                contentDescription = stringResource(id = R.string.manual_entry),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            TextView(
                text = stringResource(id = R.string.manual_entry),
                fontWeight = FontWeight.Bold,
                align = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextView(
                    stringResource(id = R.string.start_label),
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextView(
                        text = selectedStartDateText,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    startDatePicker.show()
                                }
                                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                    TextView(
                        text = selectedStartTimeText,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    startTimePicker.show()
                                }
                                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
                TextView(
                    stringResource(id = R.string.end_label),
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextView(
                        text = selectedEndDateText,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    endDatePicker.show()
                                }
                                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                    TextView(
                        text = selectedEndTimeText,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    endTimePicker.show()
                                }
                                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
                TextView(
                    stringResource(id = R.string.pause),
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilterChip(
                        onClick = { selectedPause = true },
                        label = {
                            TextView(
                                stringResource(id = R.string.yes),
                            )
                        },
                        selected = selectedPause,
                        leadingIcon = {
                            if (selectedPause) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = stringResource(id = R.string.yes),
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        },
                    )

                    FilterChip(
                        onClick = { selectedPause = false },
                        label = {
                            TextView(
                                stringResource(id = R.string.no),
                            )
                        },
                        selected = selectedPause.not(),
                        leadingIcon = {
                            if (selectedPause.not()) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(id = R.string.no),
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        },
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

                    itemEvent(ItemEvent.SetStart(selectedStart.format(formatter)))
                    itemEvent(ItemEvent.SetEnd(selectedEnd.format(formatter)))
                    itemEvent(ItemEvent.SetOngoing(false))
                    itemEvent(ItemEvent.SetPause(selectedPause))
                    itemEvent(ItemEvent.SetCategoryId(categoryState.selectedCategory))
                    itemEvent(ItemEvent.SaveItem)

                    onClose()
                },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            ) {
                TextView(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            Button(
                onClick = onClose,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
            ) {
                TextView(stringResource(id = R.string.cancel))
            }
        },
        modifier = Modifier.padding(16.dp),
    )
}

fun getFormattedTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return dateTime.format(formatter)
}
