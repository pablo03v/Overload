package cloud.pablos.overload.data.item

import androidx.compose.ui.graphics.Color
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import cloud.pablos.overload.data.Converters.Companion.convertColorToLong
import cloud.pablos.overload.data.Converters.Companion.convertStringToLocalDateTime
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.ui.tabs.home.getItemsOfDay
import cloud.pablos.overload.ui.views.extractDate
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
    val selectedCategory = categoryState.categories.find { it.id == categoryState.selectedCategory }
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

    val itemsForToday = getItemsOfDay(date, categoryState, itemState)
    val isFirstToday = itemsForToday.isEmpty()
    val isOngoingToday = itemsForToday.isNotEmpty() && itemsForToday.last().ongoing

    val itemsNotToday =
        itemState.items.filter { item ->
            val startTime = convertStringToLocalDateTime(item.startTime)
            extractDate(startTime) != date
        }
    val isOngoingNotToday = itemsNotToday.isNotEmpty() && itemsNotToday.any { it.ongoing }

    if (isOngoingNotToday) {
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
        itemEvent(ItemEvent.SetId(itemsForToday.last().id))
        itemEvent(ItemEvent.SetStart(itemsForToday.last().startTime))
        itemEvent(ItemEvent.SetEnd(LocalDateTime.now().toString()))
        itemEvent(ItemEvent.SetOngoing(false))
        itemEvent(ItemEvent.SaveItem)

        itemEvent(ItemEvent.SetIsOngoing(false))
    } else {
        itemEvent(ItemEvent.SetCategoryId(selectedCategoryId))
        itemEvent(ItemEvent.SetStart(itemsForToday.last().endTime))
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
