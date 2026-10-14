package cloud.pablos.overload.ui.tabs.home

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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import cloud.pablos.overload.data.item.Item
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.views.TextView
import cloud.pablos.overload.ui.views.getFormattedTime
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun HomeTabEditItemDialog(
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    val date = LocalDate.now()
    val dateTime = LocalDateTime.now()

    val itemsForToday = remember { getItems(categoryState, itemState, date) }

    val initialStart = remember {
        if (itemsForToday.isNotEmpty() && itemsForToday.last().endTime.isNotBlank()) {
            convertStringToLocalDateTime(itemsForToday.last().endTime)
        } else {
            dateTime
        }
    }
    val initialPause = remember {
        if (itemsForToday.isNotEmpty()) {
            itemsForToday.last().pause.not()
        } else {
            false
        }
    }

    var selectedStart by remember { mutableStateOf(initialStart) }
    var selectedEnd by remember { mutableStateOf(dateTime) }
    var selectedPause by remember { mutableStateOf(initialPause) }

    var selectedStartDateText by remember { mutableStateOf(getFormattedDate(selectedStart.toLocalDate(), true)) }
    var selectedStartTimeText by remember { mutableStateOf(getFormattedTime(selectedStart)) }
    var selectedEndDateText by remember { mutableStateOf(getFormattedDate(selectedEnd.toLocalDate(), true)) }
    var selectedEndTimeText by remember { mutableStateOf(getFormattedTime(selectedEnd)) }

    var selectedItem: Item? = null

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    selectedPause =
        when {
            selectedItem != null -> {
                selectedItem.pause
            }

            else -> {
                true
            }
        }

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
                                startTimePicker.show()
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
                                endTimePicker.show()
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
