package cloud.pablos.overload.di

import cloud.pablos.overload.data.category.CategoryRepository
import cloud.pablos.overload.data.category.CategoryRepositoryImpl
import cloud.pablos.overload.data.item.ItemRepository
import cloud.pablos.overload.data.item.ItemRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindItemRepository(
        itemRepositoryImpl: ItemRepositoryImpl
    ): ItemRepository
}
