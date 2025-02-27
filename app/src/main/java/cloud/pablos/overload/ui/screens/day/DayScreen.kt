package cloud.pablos.overload.ui.screens.day

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cloud.pablos.overload.data.Converters.Companion.convertStringToLocalDateTime
import cloud.pablos.overload.data.Helpers.Companion.getItems
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.navigation.OverloadRoute
import cloud.pablos.overload.ui.navigation.OverloadTopAppBar
import cloud.pablos.overload.ui.views.DayScreenDayView
import cloud.pablos.overload.ui.views.getLocalDate
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DayScreen(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val selectedDay = getLocalDate(itemState.selectedDayCalendar)

    val items = getItems(categoryState, itemState)

    val firstYear =
        if (items.isEmpty()) {
            LocalDate.now().year
        } else {
            items.minByOrNull { it.startTime }
                ?.let { convertStringToLocalDateTime(it.startTime).year }
                ?: LocalDate.now().year
        }

    val firstDay = LocalDate.of(firstYear, 1, 1)
    val lastDay = LocalDate.now()
    val daysCount = ChronoUnit.DAYS.between(firstDay, lastDay).toInt() + 1

    val pagerState =
        rememberPagerState(
            daysCount,
            0f,
        ) { daysCount }

    LaunchedEffect(pagerState.currentPage) {
        itemEvent(
            ItemEvent.SetSelectedDayCalendar(
                LocalDate.now()
                    .minusDays((daysCount - pagerState.currentPage - 1).toLong())
                    .toString(),
            ),
        )
    }

    var hasLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(hasLoaded) {
        if (!hasLoaded) {
            if (getLocalDate(itemState.selectedDayCalendar) != LocalDate.now()) {
                pagerState.scrollToPage(ChronoUnit.DAYS.between(firstDay, selectedDay).toInt())
            }
            hasLoaded = true
        }
    }

    Scaffold(
        topBar = { OverloadTopAppBar(OverloadRoute.DAY, categoryState, categoryEvent, itemState, itemEvent) },
    ) { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            HorizontalPager(
                pagerState,
                Modifier.padding(paddingValues),
                beyondBoundsPageCount = 2,
            ) { page ->
                DayScreenDayView(daysCount, page, categoryState, itemState, itemEvent)
            }
        }
    }
}
