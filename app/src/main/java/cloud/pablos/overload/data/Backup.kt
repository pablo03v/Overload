package cloud.pablos.overload.data

import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemState
import com.google.gson.Gson
import java.time.LocalDateTime

class Backup {
    data class DatabaseBackup(
        val data: Map<String, List<Map<String, Any>>>,
        val backupVersion: Int,
        val backupDate: String,
    )

    companion object {
        fun backupToJson(
            categoryState: CategoryState,
            itemState: ItemState,
        ): String {
            val gson = Gson()

            val categoriesTable =
                categoryState.categories.map { category ->
                    mapOf(
                        "id" to category.id,
                        "color" to category.color,
                        "emoji" to category.emoji,
                        "isDefault" to category.isDefault,
                        "name" to category.name,
                    )
                }

            val itemsTable =
                itemState.items.map { item ->
                    mapOf(
                        "id" to item.id,
                        "startTime" to item.startTime,
                        "endTime" to item.endTime,
                        "ongoing" to item.ongoing,
                        "pause" to item.pause,
                        "categoryId" to item.categoryId,
                    )
                }

            val data =
                mapOf(
                    "categories" to categoriesTable,
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
    }
}
