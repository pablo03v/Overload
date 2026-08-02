package cloud.pablos.overload.ui.tabs.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cloud.pablos.overload.data.Helpers.Companion.getFirstYear
import cloud.pablos.overload.data.Helpers.Companion.getItems
import cloud.pablos.overload.data.Helpers.Companion.getLastDay

import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.isScrollingUp
import cloud.pablos.overload.ui.navigation.OverloadRoute
import cloud.pablos.overload.ui.navigation.OverloadTopAppBar
import cloud.pablos.overload.ui.tabs.home.getFormattedDate
import cloud.pablos.overload.ui.utils.OverloadContentType
import cloud.pablos.overload.ui.utils.OverloadNavigationType
import cloud.pablos.overload.ui.views.DayScreenDayView
import cloud.pablos.overload.ui.views.TextView
import cloud.pablos.overload.ui.views.YearView
import cloud.pablos.overload.ui.views.getLocalDate
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarTab(
    navigationType: OverloadNavigationType,
    contentType: OverloadContentType,
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
    onNavigate: () -> Unit,
) {
    val listState = rememberLazyListState()
    val selectedDay = getLocalDate(itemState.selectedDayCalendar)

    Scaffold(
        topBar = {
            OverloadTopAppBar(
                OverloadRoute.CALENDAR,
                categoryState,
                categoryEvent,
                itemState,
                itemEvent,
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                navigationType == OverloadNavigationType.BOTTOM_NAVIGATION,
                enter = if (itemState.isFabOpen) slideInHorizontally(initialOffsetX = { w -> w }) else scaleIn(),
                exit = if (itemState.isFabOpen) slideOutHorizontally(targetOffsetX = { w -> w }) else scaleOut(),
            ) {
                CalendarTabFab(categoryState, itemState, itemEvent, listState.isScrollingUp())
            }
        },
    ) { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            val selectedYear by remember { mutableIntStateOf(itemState.selectedYearCalendar) }


            LaunchedEffect(selectedYear) {
                if (itemState.selectedYearCalendar != selectedDay.year) {
                    itemEvent(ItemEvent.SetSelectedYearCalendar(selectedDay.year))
                }
            }

            Column(Modifier.padding(paddingValues)) {
                AnimatedVisibility(contentType == OverloadContentType.DUAL_PANE) {
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
                    LaunchedEffect(itemState.selectedDayCalendar, daysCount) {
                        val targetPage = ChronoUnit.DAYS.between(firstDay, selectedDay).toInt()
                        if (targetPage in 0 until daysCount && pagerState.currentPage != targetPage && !pagerState.isScrollInProgress) {
                            pagerState.scrollToPage(targetPage)
                        }

                        if (selectedYear != selectedDay.year) {
                            itemEvent(ItemEvent.SetSelectedYearCalendar(selectedDay.year))
                        }
                    }


                    Row(Modifier.fillMaxSize()) {
                        Box(Modifier.weight(1f)) {
                            Column {
                                Surface(
                                    color = MaterialTheme.colorScheme.background,
                                    tonalElevation = NavigationBarDefaults.Elevation,
                                ) {
                                    WeekDaysHeader()
                                }

                                YearView(
                                    getLocalDate(itemState.selectedDayCalendar),
                                    itemState.selectedYearCalendar,
                                    categoryState,
                                    itemState,
                                    itemEvent,
                                    0.dp,
                                    true,
                                )

                            }
                        }

                        Box(Modifier.weight(1f)) {
                            HorizontalPager(
                                state = pagerState,
                                beyondViewportPageCount = 1
                            ) { page ->
                                Column {

                                    Surface(
                                        color = MaterialTheme.colorScheme.background,
                                        tonalElevation = NavigationBarDefaults.Elevation,
                                    ) {
                                        DateHeader(daysCount, page)
                                    }

                                    DayScreenDayView(daysCount, page, categoryState, itemState, itemEvent)
                                }
                            }
                        }
                    }
                }
                AnimatedVisibility(contentType == OverloadContentType.SINGLE_PANE) {
                    Column {
                        Surface(
                            color = MaterialTheme.colorScheme.background,
                            tonalElevation = NavigationBarDefaults.Elevation,
                        ) {
                            WeekDaysHeader()
                        }

                        YearView(
                            getLocalDate(itemState.selectedDayCalendar),
                            itemState.selectedYearCalendar,
                            categoryState,
                            itemState,
                            itemEvent,
                            80.dp,
                            onNavigate = onNavigate,
                            listState = listState,
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun WeekDaysHeader() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        Arrangement.SpaceBetween,
    ) {
        DayOfWeekHeaderCell("M")
        DayOfWeekHeaderCell("T")
        DayOfWeekHeaderCell("W")
        DayOfWeekHeaderCell("T")
        DayOfWeekHeaderCell("F")
        DayOfWeekHeaderCell("S")
        DayOfWeekHeaderCell("S")
    }
}

@Composable
fun DayOfWeekHeaderCell(text: String) {
    Box(
        Modifier
            .padding()
            .requiredSize(36.dp),
        Alignment.Center,
    ) {
        TextView(
            text,
            fontSize = 14.sp,
        )
    }
}

@Composable
fun DateHeader(
    daysCount: Int,
    page: Int,
) {
    val date =
        LocalDate.now()
            .minusDays((daysCount - page - 1).toLong())

    val text = getFormattedDate(date, true)

    Box(
        Modifier
            .padding()
            .requiredHeight(36.dp)
            .fillMaxWidth(),
    ) {
        TextView(
            text,
            Modifier.padding(6.dp),
            14.sp,
        )
    }
}
