package cloud.pablos.overload.data.category

sealed interface CategoryEvent {
    data object SaveCategory : CategoryEvent

    data class SetId(val id: Int) : CategoryEvent

    data class SetName(val name: String) : CategoryEvent

    // data class SetColor(val color: Color) : CategoryEvent

    // data class SetDefault(val default: Boolean) : CategoryEvent

    data class DeleteCategory(val category: Category) : CategoryEvent
}
