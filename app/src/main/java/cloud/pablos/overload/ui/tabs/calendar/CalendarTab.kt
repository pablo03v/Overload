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
import cloud.pablos.overload.data.Converters.Companion.convertStringToLocalDateTime
import cloud.pablos.overload.data.Helpers.Companion.getItems
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
            val items = getItems(categoryState, itemState)

            LaunchedEffect(selectedYear) {
                if (itemState.selectedYearCalendar != selectedDay.year) {
                    itemEvent(ItemEvent.SetSelectedYearCalendar(selectedDay.year))
                }
            }

            Column(Modifier.padding(paddingValues)) {
                AnimatedVisibility(contentType == OverloadContentType.DUAL_PANE) {
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

                    var scrollToPage = true
                    val pagerState =
                        rememberPagerState(
                            daysCount,
                            0f,
                        ) { daysCount }

                    LaunchedEffect(pagerState.currentPage) {
                        scrollToPage = false
                        itemEvent(
                            ItemEvent.SetSelectedDayCalendar(
                                LocalDate.now()
                                    .minusDays((daysCount - pagerState.currentPage - 1).toLong())
                                    .toString(),
                            ),
                        )
                    }

                    LaunchedEffect(itemState.selectedDayCalendar) {
                        if (scrollToPage) {
                            val highlightedDay = LocalDate.now().minusDays((daysCount - pagerState.currentPage - 1).toLong())
                            if (getLocalDate(itemState.selectedDayCalendar) != highlightedDay) {
                                pagerState.scrollToPage(ChronoUnit.DAYS.between(firstDay, selectedDay).toInt())
                            }
                        } else {
                            scrollToPage = true
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
                                    itemEvent,
                                    0.dp,
                                    true,
                                )
                            }
                        }

                        Box(Modifier.weight(1f)) {
                            HorizontalPager(pagerState) { page ->
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
