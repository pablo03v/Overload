package cloud.pablos.overload.data.item

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface ItemRepository {
    fun getAllItems(): Flow<List<Item>>
    suspend fun upsertItem(item: Item)
    suspend fun upsertItems(items: List<Item>)
    suspend fun deleteItems(items: List<Item>)
    suspend fun deleteAllItems()
}


class ItemRepositoryImpl @Inject constructor(private val dao: ItemDao) : ItemRepository {

    override fun getAllItems(): Flow<List<Item>> = dao.getAllItems()
    override suspend fun upsertItem(item: Item) = dao.upsertItem(item)
    override suspend fun upsertItems(items: List<Item>) = dao.upsertItems(items)
    override suspend fun deleteItems(items: List<Item>) = dao.deleteItems(items)
    override suspend fun deleteAllItems() = dao.deleteAllItems()
}

