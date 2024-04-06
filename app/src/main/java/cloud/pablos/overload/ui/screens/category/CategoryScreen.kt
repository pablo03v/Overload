package cloud.pablos.overload.ui.screens.day

import android.os.Build
import android.util.Log
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.navigation.OverloadRoute
import cloud.pablos.overload.ui.navigation.OverloadTopAppBar
import cloud.pablos.overload.ui.views.DayScreenDayView
import cloud.pablos.overload.ui.views.parseToLocalDateTime
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CategoryScreen(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    state: ItemState,
    onEvent: (ItemEvent) -> Unit,
) {
    val categories = categoryState.categories

    Log.d("test", categories.toString())

    val firstYear =
        if (state.items.isEmpty()) {
            LocalDate.now().year
        } else {
            state.items.minByOrNull { it.startTime }
                ?.let { parseToLocalDateTime(it.startTime).year }
                ?: LocalDate.now().year
        }

    val firstDay = LocalDate.of(firstYear, 1, 1)
    val lastDay = LocalDate.now()
    val daysCount = ChronoUnit.DAYS.between(firstDay, lastDay).toInt() + 1

    val pagerState =
        rememberPagerState(
            initialPage = daysCount,
            initialPageOffsetFraction = 0f,
            pageCount = { daysCount },
        )

    LaunchedEffect(pagerState.currentPage) {
        onEvent(
            ItemEvent.SetSelectedDayCalendar(
                LocalDate.now()
                    .minusDays((daysCount - pagerState.currentPage - 1).toLong())
                    .toString(),
            ),
        )
    }

    Scaffold(
        topBar = {
            OverloadTopAppBar(
                selectedDestination = OverloadRoute.DAY,
                state = state,
                onEvent = onEvent,
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                beyondBoundsPageCount = 2,
                modifier = Modifier.padding(paddingValues),
            ) { page ->
                DayScreenDayView(
                    daysCount = daysCount,
                    page = page,
                    state = state,
                    onEvent = onEvent,
                )
            }
        }
    }
}
