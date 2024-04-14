package cloud.pablos.overload.ui.tabs.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
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
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.navigation.OverloadRoute
import cloud.pablos.overload.ui.navigation.OverloadTopAppBar
import cloud.pablos.overload.ui.tabs.home.getFormattedDate
import cloud.pablos.overload.ui.utils.OverloadContentType
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
    contentType: OverloadContentType,
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
    onNavigate: () -> Unit,
) {
    Scaffold(
        topBar = {
            OverloadTopAppBar(
                selectedDestination = OverloadRoute.CALENDAR,
                categoryState = categoryState,
                categoryEvent = categoryEvent,
                itemState = itemState,
                itemEvent = itemEvent,
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(paddingValues)) {
                AnimatedVisibility(visible = contentType == OverloadContentType.DUAL_PANE) {
                    val selectedYear by remember { mutableIntStateOf(itemState.selectedYearCalendar) }
                    val selectedDay = getLocalDate(itemState.selectedDayCalendar)

                    val firstYear =
                        if (itemState.items.isEmpty()) {
                            LocalDate.now().year
                        } else {
                            itemState.items.minByOrNull { it.startTime }
                                ?.let { convertStringToLocalDateTime(it.startTime).year }
                                ?: LocalDate.now().year
                        }
                    val firstDay = LocalDate.of(firstYear, 1, 1)
                    val lastDay = LocalDate.now()

                    val daysCount = ChronoUnit.DAYS.between(firstDay, lastDay).toInt() + 1

                    var scrollToPage = true
                    val pagerState =
                        rememberPagerState(
                            initialPage = daysCount,
                            initialPageOffsetFraction = 0f,
                            pageCount = { daysCount },
                        )

                    LaunchedEffect(selectedYear) {
                        if (itemState.selectedYearCalendar != selectedDay.year) {
                            itemEvent(ItemEvent.SetSelectedYearCalendar(selectedDay.year))
                        }
                    }

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

                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            Column {
                                Surface(
                                    tonalElevation = NavigationBarDefaults.Elevation,
                                    color = MaterialTheme.colorScheme.background,
                                ) {
                                    WeekDaysHeader()
                                }

                                YearView(
                                    categoryState = categoryState,
                                    itemEvent = itemEvent,
                                    date = getLocalDate(itemState.selectedDayCalendar),
                                    year = itemState.selectedYearCalendar,
                                    bottomPadding = 0.dp,
                                    highlightSelectedDay = true,
                                )
                            }
                        }

                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            HorizontalPager(
                                state = pagerState,
                            ) { page ->
                                Column {
                                    Surface(
                                        tonalElevation = NavigationBarDefaults.Elevation,
                                        color = MaterialTheme.colorScheme.background,
                                    ) {
                                        DateHeader(
                                            daysCount = daysCount,
                                            page = page,
                                        )
                                    }

                                    DayScreenDayView(
                                        daysCount = daysCount,
                                        page = page,
                                        categoryState = categoryState,
                                        itemState = itemState,
                                        itemEvent = itemEvent,
                                    )
                                }
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = contentType == OverloadContentType.SINGLE_PANE) {
                    Column {
                        Surface(
                            tonalElevation = NavigationBarDefaults.Elevation,
                            color = MaterialTheme.colorScheme.background,
                        ) {
                            WeekDaysHeader()
                        }

                        YearView(
                            categoryState = categoryState,
                            itemEvent = itemEvent,
                            date = getLocalDate(itemState.selectedDayCalendar),
                            year = itemState.selectedYearCalendar,
                            onNavigate = onNavigate,
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
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
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
        modifier =
            Modifier
                .padding()
                .requiredSize(36.dp),
        contentAlignment = Alignment.Center,
    ) {
        TextView(
            text = text,
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
        modifier =
            Modifier
                .padding()
                .requiredHeight(36.dp)
                .fillMaxWidth(),
    ) {
        TextView(
            text = text,
            fontSize = 14.sp,
            modifier = Modifier.padding(6.dp),
        )
    }
}
