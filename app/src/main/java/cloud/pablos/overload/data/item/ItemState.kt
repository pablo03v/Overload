package cloud.pablos.overload.data.item

import java.time.LocalDateTime

data class ItemState(
    val items: List<Item> = emptyList(),
    // --
    val id: Int = 0,
    val start: String = LocalDateTime.now().toString(),
    val end: String = "",
    val ongoing: Boolean = false,
    val pause: Boolean = false,
    val isOngoing: Boolean = false,
    val categoryId: Int = 1,
    // --
    val selectedDayCalendar: String = "",
    val selectedYearCalendar: Int = 0,
    val isDeletingHome: Boolean = false,
    val isSelectedHome: Boolean = false,
    val selectedItemsHome: List<Item> = emptyList(),
    val isForgotToStopDialogShown: Boolean = false,
    val isAdjustEndDialogShown: Boolean = false,
    val isSpreadAcrossDaysDialogShown: Boolean = false,
    val isFabOpen: Boolean = false,
    val navigateToScreenRoute: String = "",
    val navigateToScreenPopBackStack: Boolean = false,
)
