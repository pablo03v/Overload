package cloud.pablos.overload.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        const val DATABASE_NAME = "items"

        @Volatile private var instance: OverloadDatabase? = null

        fun getInstance(context: Context): OverloadDatabase =
            instance
                ?: synchronized(this) {
                    instance ?: buildDatabase(context).also { instance = it }
                }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                OverloadDatabase::class.java,
                DATABASE_NAME,
            ).build()
    }
}
