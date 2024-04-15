package cloud.pablos.overload.ui

import androidx.compose.runtime.Composable
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState

data class TabItem(
    val titleResId: Int,
    val screen: @Composable (categoryState: CategoryState, itemState: ItemState, itemEvent: (ItemEvent) -> Unit) -> Unit,
)
