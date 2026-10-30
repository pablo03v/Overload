package cloud.pablos.overload.ui.tabs.home

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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import cloud.pablos.overload.ui.views.getFormattedTime
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTabEditItemDialog(
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val date = LocalDate.now()
    val dateTime = LocalDateTime.now()

    val selectedItem = remember(itemState.selectedItemsHome) {
        itemState.selectedItemsHome.firstOrNull()
    }

    val itemsForToday = remember { getItems(categoryState, itemState, date) }

    val initialStart = remember(selectedItem) {
        if (selectedItem != null && selectedItem.startTime.isNotBlank()) {
            convertStringToLocalDateTime(selectedItem.startTime)
        } else if (itemsForToday.isNotEmpty() && itemsForToday.last().endTime.isNotBlank()) {
            convertStringToLocalDateTime(itemsForToday.last().endTime)
        } else {
            dateTime
        }
    }

    val initialEnd = remember(selectedItem) {
        if (selectedItem != null && selectedItem.endTime.isNotBlank()) {
            convertStringToLocalDateTime(selectedItem.endTime)
        } else {
            dateTime
        }
    }

    val initialPause = remember(selectedItem) {
        selectedItem?.pause
            ?: (itemsForToday.isNotEmpty() && itemsForToday.last().pause.not())
    }

    var selectedStart by remember(initialStart) { mutableStateOf(initialStart) }
    var selectedEnd by remember(initialEnd) { mutableStateOf(initialEnd) }
    var selectedPause by remember(initialPause) { mutableStateOf(initialPause) }

    var selectedStartDateText by remember(selectedStart) { mutableStateOf(getFormattedDate(selectedStart.toLocalDate(), true)) }
    var selectedStartTimeText by remember(selectedStart) { mutableStateOf(getFormattedTime(selectedStart)) }
    var selectedEndDateText by remember(selectedEnd) { mutableStateOf(getFormattedDate(selectedEnd.toLocalDate(), true)) }
    var selectedEndTimeText by remember(selectedEnd) { mutableStateOf(getFormattedTime(selectedEnd)) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    fun updateSelectedTime(
        newDateTime: LocalDateTime,
        isStart: Boolean,
    ) {
        if (newDateTime <= dateTime) {
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
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedStart.toLocalDate()
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
        )

        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedLocalDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            val newStartDateTime = selectedStart
                                .withYear(selectedLocalDate.year)
                                .withMonth(selectedLocalDate.monthValue)
                                .withDayOfMonth(selectedLocalDate.dayOfMonth)

                            updateSelectedTime(newStartDateTime, true)
                        }
                        showStartDatePicker = false
                    },
                ) {
                    TextView(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStartDatePicker = false },
                ) {
                    TextView(stringResource(R.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedEnd.toLocalDate()
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
        )

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedLocalDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            val newEndDateTime = selectedEnd
                                .withYear(selectedLocalDate.year)
                                .withMonth(selectedLocalDate.monthValue)
                                .withDayOfMonth(selectedLocalDate.dayOfMonth)

                            updateSelectedTime(newEndDateTime, false)
                        }
                        showEndDatePicker = false
                    },
                ) {
                    TextView(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEndDatePicker = false },
                ) {
                    TextView(stringResource(R.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showStartTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedStart.hour,
            initialMinute = selectedStart.minute,
        )

        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newStartDateTime = selectedStart
                            .withHour(timePickerState.hour)
                            .withMinute(timePickerState.minute)

                        updateSelectedTime(newStartDateTime, true)
                        showStartTimePicker = false
                    },
                ) {
                    TextView(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStartTimePicker = false },
                ) {
                    TextView(stringResource(R.string.cancel))
                }
            },
            text = {
                TimePicker(state = timePickerState)
            },
        )
    }

    if (showEndTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedEnd.hour,
            initialMinute = selectedEnd.minute,
        )

        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newEndDateTime = selectedEnd
                            .withHour(timePickerState.hour)
                            .withMinute(timePickerState.minute)

                        updateSelectedTime(newEndDateTime, false)
                        showEndTimePicker = false
                    },
                ) {
                    TextView(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEndTimePicker = false },
                ) {
                    TextView(stringResource(R.string.cancel))
                }
            },
            text = {
                TimePicker(state = timePickerState)
            },
        )
    }

    AlertDialog(
        onDismiss,
        {
            Button(
                {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

                    if (selectedItem != null) {
                        itemEvent(ItemEvent.SetId(selectedItem.id))
                        itemEvent(ItemEvent.SetCategoryId(selectedItem.categoryId))
                    }
                    itemEvent(ItemEvent.SetStart(selectedStart.format(formatter)))
                    itemEvent(ItemEvent.SetEnd(selectedEnd.format(formatter)))
                    itemEvent(ItemEvent.SetOngoing(false))
                    itemEvent(ItemEvent.SetPause(selectedPause))

                    itemEvent(ItemEvent.SaveItem)

                    onDismiss()
                },
                colors =
                    ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            ) {
                TextView(stringResource(R.string.save))
            }
        },
        Modifier.padding(16.dp),
        {
            Button(
                onDismiss,
                colors =
                    ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
            ) {
                TextView(stringResource(R.string.cancel))
            }
        },
        {
            Icon(
                Icons.Rounded.AddCircle,
                stringResource(R.string.edit_entry),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        {
            TextView(
                stringResource(R.string.edit_entry),
                Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                align = TextAlign.Center,
            )
        },
        {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextView(
                    stringResource(R.string.start_label),
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextView(
                        selectedStartDateText,
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                showStartDatePicker = true
                            }
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    TextView(
                        selectedStartTimeText,
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                showStartTimePicker = true
                            }
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
                TextView(
                    stringResource(R.string.end_label),
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextView(
                        selectedEndDateText,
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                showEndDatePicker = true
                            }
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    TextView(
                        selectedEndTimeText,
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                showEndTimePicker = true
                            }
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
                TextView(
                    stringResource(R.string.pause),
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilterChip(
                        selectedPause,
                        { selectedPause = true },
                        {
                            TextView(
                                stringResource(R.string.yes),
                            )
                        },
                        leadingIcon = {
                            if (selectedPause) {
                                Icon(
                                    Icons.Default.Done,
                                    stringResource(R.string.yes),
                                    Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        },
                    )

                    FilterChip(
                        selectedPause.not(),
                        { selectedPause = false },
                        {
                            TextView(
                                stringResource(R.string.no),
                            )
                        },
                        leadingIcon = {
                            if (selectedPause.not()) {
                                Icon(
                                    Icons.Default.Close,
                                    stringResource(R.string.no),
                                    Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        },
                    )
                }
            }
        },
    )
}
