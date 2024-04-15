package cloud.pablos.overload.data.item

import androidx.compose.ui.graphics.Color
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import cloud.pablos.overload.data.Converters.Companion.convertColorToLong
import cloud.pablos.overload.data.Helpers.Companion.getItems
import cloud.pablos.overload.data.Helpers.Companion.getItemsPastDays
import cloud.pablos.overload.data.Helpers.Companion.getSelectedCategory
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface ItemDao {
    @Upsert
    suspend fun upsertItem(item: Item)

    /*@Upsert
    suspend fun upsertItems(items: List<Item>)

    @Delete
    suspend fun deleteItem(item: Item)*/

    @Delete
    suspend fun deleteItems(items: List<Item>)

    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<Item>>
}

fun fabPress(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val categories = categoryState.categories

    if (categories.isNotEmpty()) {
        startOrStop(categoryState, categoryEvent, itemState, itemEvent)
    } else if (itemState.items.isNotEmpty()) {
        categoryEvent(CategoryEvent.SetId(1))
        categoryEvent(CategoryEvent.SetName("Default"))
        categoryEvent(CategoryEvent.SetColor(convertColorToLong(Color(204, 230, 255))))
        categoryEvent(CategoryEvent.SetGoal1(0))
        categoryEvent(CategoryEvent.SetGoal2(0))
        categoryEvent(CategoryEvent.SetEmoji("🕣"))
        categoryEvent(CategoryEvent.SetIsDefault(true))
        categoryEvent(CategoryEvent.SaveCategory)

        startOrStop(categoryState, categoryEvent, itemState, itemEvent)
    } else {
        categoryEvent(CategoryEvent.SetIsCreateCategoryDialogOpenHome(true))
    }
}

fun startOrStop(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val date = LocalDate.now()
    val selectedCategory = getSelectedCategory(categoryState)
    var selectedCategoryId: Int?

    if (selectedCategory != null) {
        selectedCategoryId = selectedCategory.id
    } else if (categoryState.categories.isEmpty()) {
        categoryEvent(CategoryEvent.SetIsCreateCategoryDialogOpenHome(true))
        return
    } else {
        selectedCategoryId = categoryState.categories.first().id
        categoryEvent(CategoryEvent.SetSelectedCategory(selectedCategoryId))

        if (categoryState.categories.count() > 1) {
            return
        }
    }

    val itemsToday = getItems(categoryState, itemState, date)
    val isFirstToday = itemsToday.isEmpty()
    val isOngoingToday = itemsToday.isNotEmpty() && itemsToday.last().ongoing

    val itemsPastDays = getItemsPastDays(categoryState, itemState)
    val itemsOngoingNotToday = itemsPastDays.isNotEmpty() && itemsPastDays.any { it.ongoing }

    if (itemsOngoingNotToday) {
        itemEvent(ItemEvent.SetForgotToStopDialogShown(true))
    } else if (isFirstToday) {
        itemEvent(ItemEvent.SetCategoryId(selectedCategoryId))
        itemEvent(ItemEvent.SetStart(LocalDateTime.now().toString()))
        itemEvent(ItemEvent.SetOngoing(true))
        itemEvent(ItemEvent.SetPause(false))
        itemEvent(ItemEvent.SaveItem)

        itemEvent(ItemEvent.SetIsOngoing(true))
    } else if (isOngoingToday) {
        itemEvent(ItemEvent.SetCategoryId(selectedCategoryId))
        itemEvent(ItemEvent.SetId(itemsToday.last().id))
        itemEvent(ItemEvent.SetStart(itemsToday.last().startTime))
        itemEvent(ItemEvent.SetEnd(LocalDateTime.now().toString()))
        itemEvent(ItemEvent.SetOngoing(false))
        itemEvent(ItemEvent.SaveItem)

        itemEvent(ItemEvent.SetIsOngoing(false))
    } else {
        itemEvent(ItemEvent.SetCategoryId(selectedCategoryId))
        itemEvent(ItemEvent.SetStart(itemsToday.last().endTime))
        itemEvent(ItemEvent.SetEnd(LocalDateTime.now().toString()))
        itemEvent(ItemEvent.SetOngoing(false))
        itemEvent(ItemEvent.SetPause(true))
        itemEvent(ItemEvent.SaveItem)

        itemEvent(ItemEvent.SetCategoryId(selectedCategoryId))
        itemEvent(ItemEvent.SetStart(LocalDateTime.now().toString()))
        itemEvent(ItemEvent.SetOngoing(true))
        itemEvent(ItemEvent.SetPause(false))
        itemEvent(ItemEvent.SaveItem)

        itemEvent(ItemEvent.SetIsOngoing(true))
    }
}
