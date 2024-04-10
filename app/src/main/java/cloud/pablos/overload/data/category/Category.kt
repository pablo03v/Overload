package cloud.pablos.overload.data.category

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import cloud.pablos.overload.data.item.Item

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val color: String,
    val emoji: String,
    val isDefault: Boolean,
    val name: String,
)

data class CategoryWithItems(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId",
    )
    val items: List<Item>,
)
