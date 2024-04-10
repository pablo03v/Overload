package cloud.pablos.overload.data.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Upsert
    suspend fun upsertCategory(category: Category)

//    @Upsert
//    suspend fun upsertCategories(items: List<Category>)

    @Delete
    suspend fun deleteCategory(category: Category)

//    @Delete
//    suspend fun deleteCategories(items: List<Category>)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM categories")
    fun getCategoryWithItems(): Flow<List<CategoryWithItems>>
}
