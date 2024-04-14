package cloud.pablos.overload.data.category

import androidx.compose.ui.graphics.Color

data class CategoryState(
    val categories: List<Category> = emptyList(),
    val categoryWithItems: List<CategoryWithItems> = emptyList(),
    // --
    val id: Int = 1,
    val color: Long = Color.Unspecified.value.toLong(),
    val emoji: String = "🕣",
    val goal1: Int = 0,
    val goal2: Int = 0,
    val name: String = "Default",
    val isDefault: Boolean = true,
    // --
    val selectedCategoryConfigurations: Int = 1,
    val selectedCategory: Int = 1,
    val isCreateCategoryDialogOpenHome: Boolean = false,
)
