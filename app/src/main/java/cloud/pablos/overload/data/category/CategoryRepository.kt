package cloud.pablos.overload.data.category

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    fun getCategoryWithItems(): Flow<List<CategoryWithItems>>
    suspend fun insertCategory(category: Category)
    suspend fun upsertCategory(category: Category)
    suspend fun upsertCategories(categories: List<Category>)
    suspend fun deleteCategory(category: Category)
    suspend fun deleteAllCategories()
}


class CategoryRepositoryImpl @Inject constructor(private val dao: CategoryDao) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> = dao.getAllCategories()
    override fun getCategoryWithItems(): Flow<List<CategoryWithItems>> = dao.getCategoryWithItems()
    override suspend fun insertCategory(category: Category) = dao.insertCategory(category)
    override suspend fun upsertCategory(category: Category) = dao.upsertCategory(category)
    override suspend fun upsertCategories(categories: List<Category>) = dao.upsertCategories(categories)
    override suspend fun deleteCategory(category: Category) = dao.deleteCategory(category)
    override suspend fun deleteAllCategories() = dao.deleteAllCategories()
}

