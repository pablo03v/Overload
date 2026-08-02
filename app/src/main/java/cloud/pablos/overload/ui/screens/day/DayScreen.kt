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

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cloud.pablos.overload.data.Helpers.Companion.getFirstYear
import cloud.pablos.overload.data.Helpers.Companion.getItems
import cloud.pablos.overload.data.Helpers.Companion.getLastDay

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

    val firstYear = getFirstYear(itemState)
    val firstDay = LocalDate.of(firstYear, 1, 1)
    val lastDay = getLastDay(itemState)

    val daysCount = maxOf(1, ChronoUnit.DAYS.between(firstDay, lastDay).toInt() + 1)



    val initialPage = ChronoUnit.DAYS.between(firstDay, selectedDay).toInt()
    val pagerState =
        rememberPagerState(
            initialPage = if (initialPage in 0 until daysCount) initialPage else maxOf(0, daysCount - 1)
        ) { daysCount }


    // Update state ONLY when the pager settles on a new page
    LaunchedEffect(pagerState.settledPage) {
        val pageDate = firstDay.plusDays(pagerState.settledPage.toLong()).toString()
        if (pageDate != itemState.selectedDayCalendar) {
            itemEvent(ItemEvent.SetSelectedDayCalendar(pageDate))
        }
    }

    // Scroll to page ONLY if changed from outside (and not currently dragging)
    LaunchedEffect(selectedDay) {
        val targetPage = ChronoUnit.DAYS.between(firstDay, selectedDay).toInt()
        if (targetPage in 0 until daysCount && pagerState.currentPage != targetPage && !pagerState.isScrollInProgress) {
            pagerState.scrollToPage(targetPage)
        }
    }



    Scaffold(
        topBar = { OverloadTopAppBar(OverloadRoute.DAY, categoryState, categoryEvent, itemState, itemEvent) },
    ) { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            HorizontalPager(
                pagerState,
                Modifier.padding(paddingValues),
                beyondViewportPageCount = 2,

            ) { page ->
                DayScreenDayView(daysCount, page, categoryState, itemState, itemEvent)
            }
        }
    }
}
