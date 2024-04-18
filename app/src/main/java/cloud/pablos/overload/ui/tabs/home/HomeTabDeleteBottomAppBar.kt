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
import cloud.pablos.overload.data.Helpers.Companion.getItems
import cloud.pablos.overload.data.Helpers.Companion.getSelectedDay
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState

@Composable
fun HomeTabDeleteBottomAppBar(
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val date = getSelectedDay(itemState)

    val itemsForSelectedDay = getItems(categoryState, itemState, date)

    BottomAppBar(
        {
            IconButton({
                itemEvent(
                    ItemEvent.SetSelectedItemsHome(
                        itemState.selectedItemsHome +
                            itemsForSelectedDay.filterNot {
                                itemState.selectedItemsHome.contains(
                                    it,
                                )
                            },
                    ),
                )
            }) {
                Icon(
                    Icons.Filled.SelectAll,
                    stringResource(R.string.select_all_items_of_selected_day),
                )
            }
            IconButton({
                itemEvent(ItemEvent.SetSelectedItemsHome(itemState.selectedItemsHome - itemsForSelectedDay.toSet()))
            }) {
                Icon(
                    Icons.Filled.Deselect,
                    stringResource(R.string.deselect_all_items_of_selected_day),
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                {
                    itemEvent(ItemEvent.DeleteItems(itemState.selectedItemsHome))
                },
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ) {
                Icon(
                    Icons.Filled.DeleteForever,
                    stringResource(R.string.delete_items_forever),
                )
            }
        },
    )
}

@Composable
fun HomeTabDeleteFAB(
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    FloatingActionButton(
        {
            itemEvent(ItemEvent.DeleteItems(itemState.selectedItemsHome))
        },
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Icon(
            Icons.Filled.DeleteForever,
            stringResource(R.string.delete_items_forever),
        )
    }
}
