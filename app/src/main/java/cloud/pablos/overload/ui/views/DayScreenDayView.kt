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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.Helpers.Companion.getItems
import cloud.pablos.overload.data.Helpers.Companion.getSelectedCategory
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.Item
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.tabs.home.HomeTabDeletePauseDialog
import cloud.pablos.overload.ui.tabs.home.HomeTabEditItemDialog
import java.time.LocalDate

@SuppressLint("UnusedTransitionTargetStateParameter")
@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DayScreenDayView(
    daysCount: Int,
    page: Int,
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val date =
        LocalDate.now()
            .minusDays((daysCount - page - 1).toLong())

    val items = getItems(categoryState, itemState, date)
    val itemsDesc = items.sortedByDescending { it.startTime }

    val deletePauseDialogState = remember { mutableStateOf(false) }
    val editItemDialogState = remember { mutableStateOf(false) }

    val selectedCategory = getSelectedCategory(categoryState)

    if (itemsDesc.isNotEmpty() && selectedCategory != null) {
        val goal1 = selectedCategory.goal1
        val goal2 = selectedCategory.goal2

        LazyColumn(Modifier.fillMaxSize()) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 10.dp, 10.dp),
                    Arrangement.spacedBy(10.dp),
                ) {
                    if (goal1 > 0) {
                        Box(Modifier.weight(1f)) {
                            DayViewProgress(selectedCategory, items, goal1, false, date)
                        }
                    }

                    if (goal2 > 0) {
                        Box(Modifier.weight(1f)) {
                            DayViewProgress(selectedCategory, items, goal2, true, date)
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
                        Modifier
                            .padding(10.dp, 10.dp, 10.dp)
                            .combinedClickable(
                                onLongClick = {
                                    deletePauseDialogState.value = true
                                },
                                onClick = {
                                    if (itemState.isDeletingHome) {
                                        deletePauseDialogState.value = true
                                    } else {
                                        itemEvent(ItemEvent.SetSelectedItemsHome(emptyList()))
                                        editItemDialogState.value = true
                                    }
                                },
                            ),
                    ) {
                        DayViewItemOngoing(
                            Item(
                                -1,
                                items.last().endTime,
                                LocalDate.now().toString(),
                                true,
                                true,
                                categoryState.selectedCategory,
                            ),
                            categoryState,
                            itemState,
                            false,
                        )
                    }
                }
            }

            items(itemSize) { index ->
                val isFirstItem = index == 0
                val isLastItem = index == itemSize - 1

                val item = itemsDesc[index]
                val isSelected = itemState.selectedItemsHome.contains(item)
                Box(
                    Modifier
                        .padding(
                            10.dp,
                            if (isFirstItem) 10.dp else 0.dp,
                            10.dp,
                            if (isLastItem) 80.dp else 10.dp,
                        )
                        .combinedClickable(
                            onLongClick = {
                                itemEvent(ItemEvent.SetIsDeletingHome(true))
                                itemEvent(ItemEvent.SetSelectedItemsHome(listOf(item)))
                                itemEvent(ItemEvent.SetIsFabOpen(false))
                            },
                            onClick = {
                                if (itemState.isDeletingHome) {
                                    when (isSelected) {
                                        true ->
                                            itemEvent(
                                                ItemEvent.SetSelectedItemsHome(itemState.selectedItemsHome.filterNot { it == item }),
                                            )

                                        else ->
                                            itemEvent(
                                                ItemEvent.SetSelectedItemsHome(
                                                    itemState.selectedItemsHome +
                                                        listOf(
                                                            item,
                                                        ),
                                                ),
                                            )
                                    }
                                } else {
                                    itemEvent(ItemEvent.SetSelectedItemsHome(listOf(item)))
                                    editItemDialogState.value = true
                                }
                            },
                        ),
                ) {
                    when (item.ongoing.not() && item.endTime.isNotBlank()) {
                        true -> DayViewItemNotOngoing(item, categoryState, itemState, isSelected)
                        else -> DayViewItemOngoing(item, categoryState, itemState, isSelected)
                    }
                }
            }
        }
    } else {
        Column(
            Modifier.fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally,
        ) {
            TextView(
                stringResource(R.string.no_items_title),
                Modifier.padding(horizontal = 8.dp),
                MaterialTheme.typography.titleLarge.fontSize,
                FontWeight.Bold,
                MaterialTheme.colorScheme.primary,
                TextAlign.Center,
            )
            TextView(
                stringResource(R.string.no_items_subtitle),
                Modifier.padding(horizontal = 8.dp),
                MaterialTheme.typography.bodyMedium.fontSize,
                FontWeight.Medium,
                MaterialTheme.colorScheme.primary,
                TextAlign.Center,
            )
        }
    }

    if (deletePauseDialogState.value) {
        HomeTabDeletePauseDialog { deletePauseDialogState.value = false }
    }

    if (editItemDialogState.value) {
        HomeTabEditItemDialog(
            categoryState,
            itemState,
            itemEvent,
        ) {
            itemEvent(ItemEvent.SetSelectedItemsHome(emptyList()))
            editItemDialogState.value = false
        }
    }
}
