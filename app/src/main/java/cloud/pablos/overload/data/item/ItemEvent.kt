package cloud.pablos.overload.data.item

sealed interface ItemEvent {
    data object SaveItem : ItemEvent

    data class SetCategoryId(val categoryId: Int) : ItemEvent

    data class SetId(val id: Int) : ItemEvent

    data class SetStart(val start: String) : ItemEvent

    data class SetEnd(val end: String) : ItemEvent

    data class SetOngoing(val ongoing: Boolean) : ItemEvent

    data class SetPause(val pause: Boolean) : ItemEvent

    data class SetIsOngoing(val isOngoing: Boolean) : ItemEvent

    data class DeleteItems(val items: List<Item>) : ItemEvent

    data class SetSelectedDayCalendar(val day: String) : ItemEvent

    data class SetSelectedYearCalendar(val year: Int) : ItemEvent

    data class SetIsDeletingHome(val isDeleting: Boolean) : ItemEvent

    data class SetIsSelectedHome(val isSelected: Boolean) : ItemEvent

    data class SetSelectedItemsHome(val selectedItems: List<Item>) : ItemEvent

    data class SetForgotToStopDialogShown(val isForgotToStopDialogShown: Boolean) : ItemEvent

    data class SetAdjustEndDialogShown(val isAdjustEndDialogShown: Boolean) : ItemEvent

    data class SetSpreadAcrossDaysDialogShown(val isSpreadAcrossDaysDialogShown: Boolean) :
        ItemEvent

    data class SetIsFabOpen(val isFabOpen: Boolean) : ItemEvent

    data class NavigateToScreen(val route: String, val popBackStack: Boolean = false) : ItemEvent
}
