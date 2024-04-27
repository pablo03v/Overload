package cloud.pablos.overload.data.category

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.pablos.overload.data.Converters.Companion.convertColorToLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val dao: CategoryDao,
) : ViewModel() {
    private val _categories =
        dao.getAllCategories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(CategoryState())
    val state =
        combine(_state, _categories) { state, categories ->
            state.copy(
                categories = categories,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryState())

    fun categoryEvent(event: CategoryEvent) {
        when (event) {
            is CategoryEvent.DeleteCategory -> {
                viewModelScope.launch {
                    dao.deleteCategory(event.category)
                }
            }

            CategoryEvent.SaveCategory -> {
                val id = _state.value.id
                val color = _state.value.color
                val emoji = _state.value.emoji
                val goal1 = _state.value.goal1
                val goal2 = _state.value.goal2
                val isDefault = _state.value.isDefault
                val name = _state.value.name

                val category =
                    Category(
                        id = id,
                        color = color,
                        emoji = emoji,
                        goal1 = goal1,
                        goal2 = goal2,
                        isDefault = isDefault,
                        name = name,
                    )

                viewModelScope.launch {
                    dao.upsertCategory(category)
                }

                _state.update {
                    it.copy(
                        id = 0,
                        color = convertColorToLong(Color.Unspecified),
                        emoji = "🕣",
                        goal1 = 0,
                        goal2 = 0,
                        isDefault = false,
                        name = "",
                    )
                }
            }

            CategoryEvent.CreateCategory -> {
                val color = _state.value.color
                val emoji = _state.value.emoji
                val goal1 = _state.value.goal1
                val goal2 = _state.value.goal2
                val isDefault = _state.value.isDefault
                val name = _state.value.name

                val category =
                    Category(
                        color = color,
                        emoji = emoji,
                        goal1 = goal1,
                        goal2 = goal2,
                        isDefault = isDefault,
                        name = name,
                    )

                viewModelScope.launch {
                    dao.insertCategory(category)
                }

                _state.update {
                    it.copy(
                        id = 0,
                        color = convertColorToLong(Color.Unspecified),
                        emoji = "🕣",
                        goal1 = 0,
                        goal2 = 0,
                        isDefault = false,
                        name = "",
                    )
                }
            }

            is CategoryEvent.SetColor -> {
                _state.update {
                    it.copy(
                        color = event.color,
                    )
                }
            }

            is CategoryEvent.SetEmoji -> {
                _state.update {
                    it.copy(
                        emoji = event.emoji,
                    )
                }
            }

            is CategoryEvent.SetIsDefault -> {
                _state.update {
                    it.copy(
                        isDefault = event.isDefault,
                    )
                }
            }

            is CategoryEvent.SetId -> {
                _state.update {
                    it.copy(
                        id = event.id,
                    )
                }
            }

            is CategoryEvent.SetName -> {
                _state.update {
                    it.copy(
                        name = event.name,
                    )
                }
            }

            is CategoryEvent.SetSelectedCategoryConfigurations -> {
                _state.update {
                    it.copy(
                        selectedCategoryConfigurations = event.selectedCategoryConfigurations,
                    )
                }
            }

            is CategoryEvent.SetSelectedCategory -> {
                _state.update {
                    it.copy(
                        selectedCategory = event.selectedCategory,
                    )
                }
            }

            is CategoryEvent.SetIsCreateCategoryDialogOpenHome -> {
                _state.update {
                    it.copy(
                        isCreateCategoryDialogOpenHome = event.isCreateCategoryDialogOpenHome,
                    )
                }
            }

            is CategoryEvent.SetGoal1 -> {
                _state.update {
                    it.copy(
                        goal1 = event.goal1,
                    )
                }
            }
            is CategoryEvent.SetGoal2 -> {
                _state.update {
                    it.copy(
                        goal2 = event.goal2,
                    )
                }
            }

            is CategoryEvent.SetIsSwitchCategoryDialogOpenHome -> {
                _state.update {
                    it.copy(
                        isSwitchCategoryDialogOpenHome = event.isSwitchCategoryDialogOpenHome,
                    )
                }
            }
        }
    }
}
