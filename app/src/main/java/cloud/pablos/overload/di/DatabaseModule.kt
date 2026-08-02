package cloud.pablos.overload.di

import android.content.Context
import androidx.room.Room
import cloud.pablos.overload.data.OverloadDatabase
import cloud.pablos.overload.data.category.CategoryDao
import cloud.pablos.overload.data.item.ItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): OverloadDatabase {
        return Room.databaseBuilder(
            context,
            OverloadDatabase::class.java,
            "items",
        ).build()
    }

    @Provides
    fun provideCategoryDao(database: OverloadDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideItemDao(database: OverloadDatabase): ItemDao = database.itemDao()
}
