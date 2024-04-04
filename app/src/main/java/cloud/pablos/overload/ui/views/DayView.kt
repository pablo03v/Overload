package cloud.pablos.overload.ui.views

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.item.Item
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.tabs.configurations.OlSharedPreferences
import cloud.pablos.overload.ui.tabs.home.HomeTabDeletePauseDialog
import cloud.pablos.overload.ui.tabs.home.HomeTabEditItemDialog
import cloud.pablos.overload.ui.tabs.home.getItemsOfDay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField

@SuppressLint("UnusedTransitionTargetStateParameter")
@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DayView(
    state: ItemState,
    onEvent: (ItemEvent) -> Unit,
    date: LocalDate,
) {
    val items = getItemsOfDay(date, state)

    val itemsDesc = items.sortedByDescending { it.startTime }

    val deletePauseDialogState = remember { mutableStateOf(false) }
    val editItemDialogState = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sharedPreferences = remember { OlSharedPreferences(context) }

    val goalWork by remember { mutableIntStateOf(sharedPreferences.getWorkGoal()) }
    val goalPause by remember { mutableIntStateOf(sharedPreferences.getPauseGoal()) }

    if (itemsDesc.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (goalWork > 0) {
                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            DayViewProgress(goal = goalWork, items = items, isPause = false)
                        }
                    }

                    if (goalPause > 0) {
                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            DayViewProgress(
                                goal = goalPause,
                                items = items,
                                date = date,
                                isPause = true,
                            )
                        }
                    }
                }
            }
            val itemSize = itemsDesc.size
            if (
                items.isNotEmpty() &&
                items.last().ongoing.not() &&
                items.last().pause.not() &&
                date == LocalDate.now()
            ) {
                item {
                    Box(
                        modifier =
                            Modifier
                                .padding(10.dp, 10.dp, 10.dp)
                                .combinedClickable(
                                    onLongClick = {
                                        deletePauseDialogState.value = true
                                    },
                                    onClick = {
                                        if (state.isDeletingHome) {
                                            deletePauseDialogState.value = true
                                        } else {
                                            onEvent(ItemEvent.SetSelectedItemsHome(emptyList()))
                                            editItemDialogState.value = true
                                        }
                                    },
                                ),
                    ) {
                        DayViewItemOngoing(
                            item =
                                Item(
                                    id = -1,
                                    startTime = items.last().endTime,
                                    endTime = LocalDate.now().toString(),
                                    ongoing = true,
                                    pause = true,
                                ),
                            isSelected = false,
                            state = state,
                        )
                    }
                }
            }

            items(itemSize) { index ->
                val isFirstItem = index == 0
                val isLastItem = index == itemSize - 1

                val item = itemsDesc[index]
                val isSelected = state.selectedItemsHome.contains(item)
                Box(
                    modifier =
                        Modifier
                            .padding(
                                10.dp,
                                if (isFirstItem) 10.dp else 0.dp,
                                10.dp,
                                if (isLastItem) 80.dp else 10.dp,
                            )
                            .combinedClickable(
                                onLongClick = {
                                    onEvent(ItemEvent.SetIsDeletingHome(true))
                                    onEvent(ItemEvent.SetSelectedItemsHome(listOf(item)))
                                    onEvent(ItemEvent.SetIsFabOpen(false))
                                },
                                onClick = {
                                    if (state.isDeletingHome) {
                                        when (isSelected) {
                                            true ->
                                                onEvent(
                                                    ItemEvent.SetSelectedItemsHome(state.selectedItemsHome.filterNot { it == item }),
                                                )

                                            else ->
                                                onEvent(
                                                    ItemEvent.SetSelectedItemsHome(
                                                        state.selectedItemsHome +
                                                            listOf(
                                                                item,
                                                            ),
                                                    ),
                                                )
                                        }
                                    } else {
                                        onEvent(ItemEvent.SetSelectedItemsHome(listOf(item)))
                                        editItemDialogState.value = true
                                    }
                                },
                            ),
                ) {
                    when (item.ongoing.not() && item.endTime.isNotBlank()) {
                        true -> DayViewItemNotOngoing(item, isSelected = isSelected, state)
                        else -> DayViewItemOngoing(item, isSelected = isSelected, state = state)
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextView(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(id = R.string.no_items_title),
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold,
                align = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
            )
            TextView(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(id = R.string.no_items_subtitle),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Medium,
                align = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }

    if (deletePauseDialogState.value) {
        HomeTabDeletePauseDialog(onClose = { deletePauseDialogState.value = false })
    }

    if (editItemDialogState.value) {
        HomeTabEditItemDialog(
            onClose = {
                onEvent(ItemEvent.SetSelectedItemsHome(emptyList()))
                editItemDialogState.value = false
            },
            state,
            onEvent,
        )
    }
}

fun parseToLocalDateTime(dateTimeString: String): LocalDateTime {
    val formatter =
        DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
            .optionalEnd()
            .toFormatter()

    return try {
        LocalDateTime.parse(dateTimeString, formatter)
    } catch (e: DateTimeParseException) {
        return LocalDateTime.now()
    }
}

fun getLocalDate(selectedDay: String): LocalDate {
    val date: LocalDate =
        if (selectedDay.isNotBlank()) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            LocalDate.parse(selectedDay, formatter)
        } else {
            LocalDate.now()
        }

    return date
}

fun extractDate(localDateTime: LocalDateTime): LocalDate {
    return localDateTime.toLocalDate()
}
