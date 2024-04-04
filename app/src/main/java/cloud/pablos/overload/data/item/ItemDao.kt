package cloud.pablos.overload.data.item

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import cloud.pablos.overload.ui.tabs.home.getItemsOfDay
import cloud.pablos.overload.ui.views.extractDate
import cloud.pablos.overload.ui.views.parseToLocalDateTime
import com.google.gson.Gson
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

data class DatabaseBackup(
    val data: Map<String, List<Map<String, Any>>>,
    val backupVersion: Int,
    val backupDate: String,
)

// Export function
fun backupItemsToJson(state: ItemState): String {
    val gson = Gson()

    val itemsTable =
        state.items.map { item ->
            mapOf(
                "id" to item.id,
                "startTime" to item.startTime,
                "endTime" to item.endTime,
                "ongoing" to item.ongoing,
                "pause" to item.pause,
            )
        }

    val data =
        mapOf(
            "items" to itemsTable,
        )

    val backup =
        DatabaseBackup(
            data,
            2,
            LocalDateTime.now().toString(),
        )

    return try {
        val json = gson.toJson(backup)
        json
    } catch (e: Exception) {
        "{}"
    }
}

fun startOrStopPause(
    state: ItemState,
    onEvent: (ItemEvent) -> Unit,
) {
    val date = LocalDate.now()

    val itemsForToday = getItemsOfDay(date, state)
    val isFirstToday = itemsForToday.isEmpty()
    val isOngoingToday = itemsForToday.isNotEmpty() && itemsForToday.last().ongoing

    val itemsNotToday =
        state.items.filter { item ->
            val startTime = parseToLocalDateTime(item.startTime)
            extractDate(startTime) != date
        }
    val isOngoingNotToday = itemsNotToday.isNotEmpty() && itemsNotToday.any { it.ongoing }

    if (isOngoingNotToday) {
        onEvent(ItemEvent.SetForgotToStopDialogShown(true))
    } else if (isFirstToday) {
        onEvent(ItemEvent.SetStart(LocalDateTime.now().toString()))
        onEvent(ItemEvent.SetOngoing(true))
        onEvent(ItemEvent.SetPause(false))
        onEvent(ItemEvent.SaveItem)

        onEvent(ItemEvent.SetIsOngoing(true))
    } else if (isOngoingToday) {
        onEvent(ItemEvent.SetId(itemsForToday.last().id))
        onEvent(ItemEvent.SetStart(itemsForToday.last().startTime))
        onEvent(ItemEvent.SetEnd(LocalDateTime.now().toString()))
        onEvent(ItemEvent.SetOngoing(false))
        onEvent(ItemEvent.SaveItem)

        onEvent(ItemEvent.SetIsOngoing(false))
    } else {
        onEvent(ItemEvent.SetStart(itemsForToday.last().endTime))
        onEvent(ItemEvent.SetEnd(LocalDateTime.now().toString()))
        onEvent(ItemEvent.SetOngoing(false))
        onEvent(ItemEvent.SetPause(true))
        onEvent(ItemEvent.SaveItem)

        onEvent(ItemEvent.SetStart(LocalDateTime.now().toString()))
        onEvent(ItemEvent.SetOngoing(true))
        onEvent(ItemEvent.SetPause(false))
        onEvent(ItemEvent.SaveItem)

        onEvent(ItemEvent.SetIsOngoing(true))
    }
}
