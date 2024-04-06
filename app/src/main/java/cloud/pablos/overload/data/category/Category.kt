package cloud.pablos.overload.data.category

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    // val color: Color,
    // val default: Boolean,
)
