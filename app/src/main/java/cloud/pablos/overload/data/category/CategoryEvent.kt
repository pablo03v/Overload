package cloud.pablos.overload.data.category

sealed interface CategoryEvent {
    data object SaveCategory : CategoryEvent

    data object CreateCategory : CategoryEvent

    data class SetId(val id: Int) : CategoryEvent

    data class SetColor(val color: Long) : CategoryEvent

    data class SetEmoji(val emoji: String) : CategoryEvent

    data class SetGoal1(val goal1: Int) : CategoryEvent

    data class SetGoal2(val goal2: Int) : CategoryEvent

    data class SetName(val name: String) : CategoryEvent

    data class SetIsDefault(val isDefault: Boolean) : CategoryEvent

    data class DeleteCategory(val category: Category) : CategoryEvent

    data class SetSelectedCategoryConfigurations(val selectedCategoryConfigurations: Int) : CategoryEvent

    data class SetSelectedCategory(val selectedCategory: Int) : CategoryEvent

    data class SetIsCreateCategoryDialogOpenHome(val isCreateCategoryDialogOpenHome: Boolean) : CategoryEvent

    data class SetIsSwitchCategoryDialogOpenHome(val isSwitchCategoryDialogOpenHome: Boolean) : CategoryEvent
}
