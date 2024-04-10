package cloud.pablos.overload.data.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ItemViewModel(
    private val dao: ItemDao,
) : ViewModel() {
    private val _items =
        dao.getAllItems().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(ItemState())
    val state =
        combine(_state, _items) { state, items ->
            state.copy(
                items = items,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ItemState())

    fun itemEvent(event: ItemEvent) {
        when (event) {
            is ItemEvent.DeleteItems -> {
                viewModelScope.launch {
                    dao.deleteItems(event.items)

                    _state.update {
                        it.copy(
                            isDeletingHome = false,
                            selectedItemsHome = emptyList(),
                        )
                    }
                }
            }

            ItemEvent.SaveItem -> {
                val id = _state.value.id
                val start = _state.value.start
                val end = _state.value.end
                val ongoing = _state.value.ongoing
                val pause = _state.value.pause
                val categoryId = _state.value.categoryId

                val item =
                    Item(
                        id = id,
                        startTime = start,
                        endTime = end,
                        ongoing = ongoing,
                        pause = pause,
                        categoryId = categoryId,
                    )

                viewModelScope.launch {
                    dao.upsertItem(item)
                }

                _state.update {
                    it.copy(
                        id = 0,
                        start = LocalDateTime.now().toString(),
                        end = "",
                        ongoing = false,
                        pause = false,
                    )
                }
            }

            is ItemEvent.SetStart -> {
                _state.update {
                    it.copy(
                        start = event.start,
                    )
                }
            }

            is ItemEvent.SetEnd -> {
                _state.update {
                    it.copy(
                        end = event.end,
                    )
                }
            }

            is ItemEvent.SetOngoing -> {
                _state.update {
                    it.copy(
                        ongoing = event.ongoing,
                    )
                }
            }

            is ItemEvent.SetPause -> {
                _state.update {
                    it.copy(
                        pause = event.pause,
                    )
                }
            }

            is ItemEvent.SetIsOngoing -> {
                _state.update {
                    it.copy(
                        isOngoing = event.isOngoing,
                    )
                }
            }

            is ItemEvent.SetId -> {
                _state.update {
                    it.copy(
                        id = event.id,
                    )
                }
            }

            is ItemEvent.SetSelectedDayCalendar -> {
                _state.update { it.copy(selectedDayCalendar = event.day) }
            }

            is ItemEvent.SetSelectedYearCalendar -> {
                _state.update { it.copy(selectedYearCalendar = event.year) }
            }

            is ItemEvent.SetIsDeletingHome -> {
                _state.update { it.copy(isDeletingHome = event.isDeleting) }

                when (event.isDeleting) {
                    false -> _state.update { it.copy(selectedItemsHome = emptyList()) }
                    else -> {}
                }
            }

            is ItemEvent.SetIsSelectedHome -> {
                _state.update {
                    it.copy(
                        isSelectedHome = event.isSelected,
                    )
                }
            }

            is ItemEvent.SetSelectedItemsHome -> {
                _state.update { it.copy(selectedItemsHome = event.selectedItems) }
            }

            is ItemEvent.SetForgotToStopDialogShown -> {
                _state.update {
                    it.copy(
                        isForgotToStopDialogShown = event.isForgotToStopDialogShown,
                    )
                }
            }

            is ItemEvent.SetAdjustEndDialogShown -> {
                _state.update {
                    it.copy(
                        isAdjustEndDialogShown = event.isAdjustEndDialogShown,
                    )
                }
            }

            is ItemEvent.SetSpreadAcrossDaysDialogShown -> {
                _state.update {
                    it.copy(
                        isSpreadAcrossDaysDialogShown = event.isSpreadAcrossDaysDialogShown,
                    )
                }
            }

            is ItemEvent.SetIsFabOpen -> {
                _state.update {
                    it.copy(
                        isFabOpen = event.isFabOpen,
                    )
                }
            }

            is ItemEvent.NavigateToScreen -> {
                _state.update {
                    it.copy(
                        navigateToScreenRoute = event.route,
                        navigateToScreenPopBackStack = event.popBackStack,
                    )
                }
            }

            is ItemEvent.SetCategoryId -> {
                _state.update {
                    it.copy(
                        categoryId = event.categoryId,
                    )
                }
            }
        }
    }
}
