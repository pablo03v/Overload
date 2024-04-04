package cloud.pablos.overload.ui.tabs.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cloud.pablos.overload.R
import cloud.pablos.overload.data.item.Item
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.views.extractDate
import cloud.pablos.overload.ui.views.getLocalDate
import cloud.pablos.overload.ui.views.parseToLocalDateTime
import java.time.LocalDate

@Composable
fun HomeTabDeleteBottomAppBar(
    state: ItemState,
    onEvent: (ItemEvent) -> Unit,
) {
    val date = getSelectedDay(state)

    val itemsForSelectedDay = getItemsOfDay(date, state)

    BottomAppBar(
        actions = {
            IconButton(onClick = {
                onEvent(
                    ItemEvent.SetSelectedItemsHome(
                        state.selectedItemsHome +
                            itemsForSelectedDay.filterNot {
                                state.selectedItemsHome.contains(
                                    it,
                                )
                            },
                    ),
                )
            }) {
                Icon(
                    Icons.Filled.SelectAll,
                    contentDescription = stringResource(id = R.string.select_all_items_of_selected_day),
                )
            }
            IconButton(onClick = {
                onEvent(ItemEvent.SetSelectedItemsHome(state.selectedItemsHome - itemsForSelectedDay.toSet()))
            }) {
                Icon(
                    Icons.Filled.Deselect,
                    contentDescription = stringResource(id = R.string.deselect_all_items_of_selected_day),
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(ItemEvent.DeleteItems(state.selectedItemsHome))
                },
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ) {
                Icon(
                    Icons.Filled.DeleteForever,
                    contentDescription = stringResource(id = R.string.delete_items_forever),
                )
            }
        },
    )
}

@Composable
fun HomeTabDeleteFAB(
    state: ItemState,
    onEvent: (ItemEvent) -> Unit,
) {
    FloatingActionButton(
        onClick = {
            onEvent(ItemEvent.DeleteItems(state.selectedItemsHome))
        },
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Icon(
            Icons.Filled.DeleteForever,
            contentDescription = stringResource(id = R.string.delete_items_forever),
        )
    }
}

fun getSelectedDay(state: ItemState): LocalDate {
    return state.selectedDayCalendar.takeIf { it.isNotBlank() }?.let { getLocalDate(it) }
        ?: LocalDate.now()
}

fun getItemsOfDay(
    date: LocalDate,
    state: ItemState,
): List<Item> {
    return state.items.filter { item ->
        val startTime = parseToLocalDateTime(item.startTime)
        extractDate(startTime) == date
    }
}
