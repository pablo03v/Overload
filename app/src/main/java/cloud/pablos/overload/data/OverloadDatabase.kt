package cloud.pablos.overload.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import cloud.pablos.overload.data.category.Category
import cloud.pablos.overload.data.category.CategoryDao
import cloud.pablos.overload.data.item.Item
import cloud.pablos.overload.data.item.ItemDao

@Database(
    entities = [Category::class, Item::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
)
abstract class OverloadDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao

    abstract fun itemDao(): ItemDao
}
