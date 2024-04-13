package cloud.pablos.overload.ui.tabs.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import cloud.pablos.overload.data.Helpers
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.navigation.OverloadRoute
import cloud.pablos.overload.ui.navigation.OverloadTopAppBar
import cloud.pablos.overload.ui.tabs.configurations.ConfigurationsTabCreateCategoryDialog
import cloud.pablos.overload.ui.utils.OverloadNavigationType
import cloud.pablos.overload.ui.views.TextView
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeTab(
    navigationType: OverloadNavigationType,
    categoryEvent: (CategoryEvent) -> Unit,
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val backgroundColor = Helpers.decideBackground(categoryState)

    val pagerState =
        rememberPagerState(
            initialPage = 2,
            initialPageOffsetFraction = 0f,
            pageCount = { homeTabItems.size },
        )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        val selectedDayString =
            when (pagerState.currentPage) {
                0 -> getFormattedDate(daysBefore = 2)
                1 -> getFormattedDate(daysBefore = 1)
                2 -> getFormattedDate()
                else -> getFormattedDate()
            }
        itemEvent(ItemEvent.SetSelectedDayCalendar(selectedDayString))
    }

    Scaffold(
        topBar = {
            OverloadTopAppBar(
                selectedDestination = OverloadRoute.HOME,
                categoryState = categoryState,
                itemState = itemState,
                itemEvent = itemEvent,
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible =
                    navigationType == OverloadNavigationType.BOTTOM_NAVIGATION &&
                        itemState.selectedDayCalendar == LocalDate.now().toString() &&
                        itemState.isDeletingHome.not(),
                enter = if (itemState.isFabOpen) slideInHorizontally(initialOffsetX = { w -> w }) else scaleIn(),
                exit = if (itemState.isFabOpen) slideOutHorizontally(targetOffsetX = { w -> w }) else scaleOut(),
            ) {
                HomeTabFab(categoryEvent = categoryEvent, categoryState = categoryState, itemState = itemState, itemEvent = itemEvent)
            }
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(paddingValues)) {
                Surface(
                    tonalElevation = NavigationBarDefaults.Elevation,
                    color = MaterialTheme.colorScheme.background,
                ) {
                    PrimaryTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        divider = {},
                        indicator = {
                            TabRowDefaults.PrimaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(pagerState.currentPage, matchContentSize = true),
                                width = Dp.Unspecified,
                                color = backgroundColor,
                            )
                        },
                    ) {
                        homeTabItems.forEachIndexed { index, item ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                },
                                text = {
                                    TextView(
                                        text = stringResource(id = item.titleResId),
                                        fontSize = MaterialTheme.typography.labelLarge.fontSize,
                                        fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                },
                            )
                        }
                    }
                }
                HorizontalPager(
                    state = pagerState,
                ) { page ->
                    val item = homeTabItems[page]
                    item.screen(categoryState, itemState, itemEvent)
                }
            }
        }
    }

    if (categoryState.isCreateCategoryDialogOpenHome) {
        ConfigurationsTabCreateCategoryDialog(
            onClose = {
                categoryEvent(CategoryEvent.SetIsCreateCategoryDialogOpenHome(false))
            },
            categoryEvent,
        )
    }
}

fun getFormattedDate(
    date: LocalDate,
    human: Boolean = false,
): String {
    val formatter = DateTimeFormatter.ofPattern(if (human) "dd.MM.yyyy" else "yyyy-MM-dd")
    return date.format(formatter)
}

fun getFormattedDate(daysBefore: Long): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.now().minusDays(daysBefore)
    return date.format(formatter)
}

fun getFormattedDate(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.now()
    return date.format(formatter)
}
