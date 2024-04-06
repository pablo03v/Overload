package cloud.pablos.overload.data.category

data class CategoryState(
    val categories: List<Category> = emptyList(),
    val id: Int = 0,
    val name: String = "Default",
    // val color: Color = Color.Unspecified,
    // val default: Boolean = true,
)
