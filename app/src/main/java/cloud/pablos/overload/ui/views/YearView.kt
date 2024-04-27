package cloud.pablos.overload.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cloud.pablos.overload.data.Helpers.Companion.decideBackground
import cloud.pablos.overload.data.Helpers.Companion.decideForeground
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.ui.tabs.home.getFormattedDate
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun YearView(
    date: LocalDate,
    year: Int,
    categoryState: CategoryState,
    itemEvent: (ItemEvent) -> Unit,
    bottomPadding: Dp = 0.dp,
    highlightSelectedDay: Boolean = false,
    onNavigate: () -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
) {
    val currentYear = LocalDate.now().year
    val months =
        if (year == currentYear) {
            Month.entries.toTypedArray().takeWhile { it <= LocalDate.now().month }.reversed()
        } else {
            Month.entries.reversed()
        }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        listState,
    ) {
        months.forEachIndexed { monthIndex, month ->
            item {
                MonthNameHeader(month)
            }
            val firstDayOfMonth = LocalDate.of(year, month, 1)
            val emptyCells = (firstDayOfMonth.dayOfWeek.value + 6) % 7
            val daysInMonth = firstDayOfMonth.month.length(firstDayOfMonth.isLeapYear) + emptyCells
            val weeksInMonth = daysInMonth / 7 + if (daysInMonth % 7 > 0) 1 else 0
            val isLastMonth = monthIndex == months.lastIndex

            (0 until weeksInMonth).reversed().forEachIndexed { weekIndex, weekOfMonth ->
                val isLastWeekInLastMonth = isLastMonth && weeksInMonth - weekIndex == 1

                item {
                    Box(
                        Modifier.padding(
                            0.dp,
                            0.dp,
                            0.dp,
                            if (isLastWeekInLastMonth) bottomPadding else 0.dp,
                        ),
                    ) {
                        WeekRow(month, firstDayOfMonth, weekOfMonth, date, highlightSelectedDay, categoryState, itemEvent, onNavigate)
                    }
                }
            }
        }
    }
}

@Composable
fun MonthNameHeader(month: Month) {
    TextView(
        month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        24.sp,
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun WeekRow(
    month: Month,
    firstDayOfMonth: LocalDate,
    weekOfMonth: Int,
    date: LocalDate,
    highlightSelectedDay: Boolean = false,
    categoryState: CategoryState,
    itemEvent: (ItemEvent) -> Unit,
    onNavigate: () -> Unit,
) {
    var startOfWeek = firstDayOfMonth.plusWeeks(weekOfMonth.toLong())
    val emptyCells = (startOfWeek.dayOfWeek.value + 6) % 7

    startOfWeek =
        if (weekOfMonth == 0) startOfWeek else startOfWeek.minusDays((emptyCells).toLong())
    val endDayOfWeek =
        if (weekOfMonth == 0) {
            startOfWeek.plusDays((7 - emptyCells).toLong())
        } else {
            startOfWeek.plusDays(
                (7).toLong(),
            )
        }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        Arrangement.SpaceBetween,
    ) {
        if (weekOfMonth == 0) {
            repeat(emptyCells) {
                EmptyDayCell()
            }
        }

        var iterationDate = startOfWeek
        val today = LocalDate.now()

        while (iterationDate < endDayOfWeek) {
            if (iterationDate.month == month) {
                val colors =
                    getColorOfDay(
                        categoryState,
                        iterationDate,
                        firstDayOfMonth,
                        date == iterationDate,
                        highlightSelectedDay,
                    )
                val number = iterationDate.dayOfMonth.toString()
                val clickable = iterationDate <= today

                DayCell(iterationDate, itemEvent, colors, number, clickable, onNavigate)
            } else {
                EmptyDayCell()
            }

            iterationDate = iterationDate.plusDays(1)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DayCell(
    date: LocalDate,
    itemEvent: (ItemEvent) -> Unit,
    colors: DayCellColors,
    number: String,
    clickable: Boolean,
    onNavigate: () -> Unit,
) {
    Box(
        Modifier
            .padding()
            .requiredSize(36.dp)
            .background(colors.background, CircleShape)
            .combinedClickable(
                remember { MutableInteractionSource() },
                rememberRipple(
                    radius = 18.dp,
                ),
                clickable,
                onClick = {
                    itemEvent(ItemEvent.SetSelectedDayCalendar(getFormattedDate(date)))
                    itemEvent(ItemEvent.SetIsSelectedHome(true))
                    onNavigate()
                },
            )
            .clip(CircleShape)
            .border(3.dp, colors.borderColor, CircleShape),
        Alignment.Center,
    ) {
        TextView(
            number,
            fontSize = 14.sp,
            color = colors.foreground,
        )
    }
}

@Composable
fun EmptyDayCell() {
    Box(
        Modifier
            .padding()
            .requiredSize(36.dp)
            .background(Color.Transparent, CircleShape)
            .clip(CircleShape)
            .border(3.dp, Color.Transparent, CircleShape),
    )
}

data class DayCellColors(val foreground: Color, val background: Color, val borderColor: Color)

@Composable
fun getColorOfDay(
    categoryState: CategoryState,
    date: LocalDate,
    firstDayOfMonth: LocalDate,
    selected: Boolean,
    highlightSelectedDay: Boolean = false,
): DayCellColors {
    val month = firstDayOfMonth.month
    val today = LocalDate.now()

    var backgroundColor = Color.Transparent
    var foregroundColor = Color.Unspecified
    var borderColor = Color.Transparent

    if (selected && highlightSelectedDay) {
        backgroundColor = decideBackground(categoryState)
        foregroundColor = decideForeground(backgroundColor)
    } else if (date <= today && date.month == month) {
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    }

    if (date == LocalDate.now()) {
        borderColor = decideBackground(categoryState)
    }

    return DayCellColors(foregroundColor, backgroundColor, borderColor)
}
