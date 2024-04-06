package cloud.pablos.overload.data.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun onEvent(event: CategoryEvent) {
        when (event) {
            is CategoryEvent.DeleteCategory -> {
                viewModelScope.launch {
                    dao.deleteCategory(event.category)

                    /*_state.update {
                        it.copy(
                            // TODO: If current category, then switch to default
                        )
                    }*/
                }
            }

            CategoryEvent.SaveCategory -> {
                val id = _state.value.id
                val name = _state.value.name
                // val color = _state.value.color
                // val default = _state.value.default

                val category =
                    Category(
                        id = id,
                        name = name,
                        // color = color,
                        // default = default,
                    )

                viewModelScope.launch {
                    dao.upsertCategory(category)
                }

                _state.update {
                    it.copy(
                        id = 0,
                        name = "",
                        // color = Color.Unspecified,
                        // default = false,
                    )
                }
            }

//            is CategoryEvent.SetColor -> {
//                _state.update {
//                    it.copy(
//                        color = event.color,
//                    )
//                }
//            }

//            is CategoryEvent.SetDefault -> {
//                _state.update {
//                    it.copy(
//                        default = event.default,
//                    )
//                }
//            }

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
        }
    }
}
