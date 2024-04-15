package cloud.pablos.overload.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.navigation.NavHostController
import cloud.pablos.overload.R
import cloud.pablos.overload.data.Helpers.Companion.getItems
import cloud.pablos.overload.data.Helpers.Companion.getSelectedDay
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.screens.category.CategoryScreenBottomAppBar
import cloud.pablos.overload.ui.screens.category.CategoryScreenTopAppBar
import cloud.pablos.overload.ui.screens.day.DayScreenBottomAppBar
import cloud.pablos.overload.ui.screens.day.DayScreenTopAppBar
import cloud.pablos.overload.ui.tabs.calendar.CalendarTabTopAppBar
import cloud.pablos.overload.ui.tabs.configurations.ConfigurationsTabTopAppBar
import cloud.pablos.overload.ui.tabs.home.HomeTabDeleteBottomAppBar
import cloud.pablos.overload.ui.tabs.home.HomeTabTopAppBar
import cloud.pablos.overload.ui.utils.OverloadNavigationContentPosition
import cloud.pablos.overload.ui.views.DeleteTopAppBar
import cloud.pablos.overload.ui.views.TextView

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun OverloadNavigationRail(
    selectedDestination: String,
    navigationContentPosition: OverloadNavigationContentPosition,
    navigateToTopLevelDestination: (OverloadTopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {},
    categoryEvent: (CategoryEvent) -> Unit,
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    NavigationRail(
        modifier = Modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.inverseOnSurface,
    ) {
        Layout(
            modifier = Modifier.widthIn(max = 80.dp),
            content = {
                Column(
                    modifier = Modifier.layoutId(LayoutType.HEADER).padding(bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    AnimatedVisibility(
                        visible = true,
                    ) {
                        NavigationRailItem(
                            selected = false,
                            onClick = onDrawerClicked,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = stringResource(id = R.string.navigation_drawer),
                                )
                            },
                        )
                    }

                    OverloadNavigationFabSmall(
                        categoryEvent = categoryEvent,
                        categoryState = categoryState,
                        itemState = itemState,
                        itemEvent = itemEvent,
                    )
                }
                Column(modifier = Modifier.layoutId(LayoutType.CONTENT)) {
                    when (itemState.isDeletingHome) {
                        true -> {
                            val date = getSelectedDay(itemState)
                            val items = getItems(categoryState, itemState, date)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                NavigationRailItem(
                                    selected = false,
                                    onClick = {
                                        itemEvent(
                                            ItemEvent.SetSelectedItemsHome(
                                                itemState.selectedItemsHome +
                                                    items.filterNot {
                                                        itemState.selectedItemsHome.contains(
                                                            it,
                                                        )
                                                    },
                                            ),
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Filled.SelectAll,
                                            contentDescription = stringResource(id = R.string.select_all_items_of_selected_day),
                                        )
                                    },
                                )

                                NavigationRailItem(
                                    selected = false,
                                    onClick = {
                                        itemEvent(ItemEvent.SetSelectedItemsHome(itemState.selectedItemsHome - items.toSet()))
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Filled.Deselect,
                                            contentDescription = stringResource(id = R.string.deselect_all_items_of_selected_day),
                                        )
                                    },
                                )
                            }
                        }

                        false -> {
                            AnimatedVisibility(visible = itemState.isFabOpen.not()) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    TOP_LEVEL_DESTINATIONS.forEach { overloadDestination ->
                                        NavigationRailItem(
                                            selected = selectedDestination == overloadDestination.route,
                                            onClick = {
                                                navigateToTopLevelDestination(
                                                    overloadDestination,
                                                )
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector =
                                                        if (selectedDestination == overloadDestination.route) {
                                                            overloadDestination.selectedIcon
                                                        } else {
                                                            overloadDestination.unselectedIcon
                                                        },
                                                    contentDescription = stringResource(id = overloadDestination.iconTextId),
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            measurePolicy = navigationMeasurePolicy(navigationContentPosition),
        )
    }
}

class BottomBarState private constructor() {
    companion object {
        val Normal = BottomBarState()
        val Deleting = BottomBarState()

        val Category = BottomBarState()
        val Day = BottomBarState()
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun OverloadBottomNavigationBar(
    selectedDestination: String,
    navigateToTopLevelDestination: (OverloadTopLevelDestination) -> Unit,
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
    navController: NavHostController,
) {
    var currentBottomBarState by remember { mutableStateOf(BottomBarState.Normal) }

    DisposableEffect(itemState.isDeletingHome, selectedDestination) {
        currentBottomBarState =
            when (selectedDestination) {
                OverloadRoute.HOME -> {
                    when (itemState.isDeletingHome) {
                        true -> BottomBarState.Deleting
                        false -> BottomBarState.Normal
                    }
                }

                OverloadRoute.CATEGORY -> {
                    BottomBarState.Category
                }

                OverloadRoute.DAY -> {
                    when (itemState.isDeletingHome) {
                        true -> BottomBarState.Deleting
                        false -> BottomBarState.Day
                    }
                }

                else -> BottomBarState.Normal
            }
        onDispose {
            currentBottomBarState = BottomBarState.Normal
        }
    }

    for (bottomBarState in listOf(
        BottomBarState.Normal,
        BottomBarState.Category,
        BottomBarState.Day,
        BottomBarState.Deleting,
    )) {
        AnimatedVisibility(
            visible = bottomBarState == currentBottomBarState,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            when (bottomBarState) {
                BottomBarState.Normal -> {
                    NavigationBar(modifier = Modifier.fillMaxWidth()) {
                        TOP_LEVEL_DESTINATIONS.forEach { overloadDestination ->
                            NavigationBarItem(
                                selected = selectedDestination == overloadDestination.route,
                                onClick = {
                                    navigateToTopLevelDestination(
                                        overloadDestination,
                                    )
                                },
                                icon = {
                                    Icon(
                                        imageVector =
                                            if (selectedDestination == overloadDestination.route) {
                                                overloadDestination.selectedIcon
                                            } else {
                                                overloadDestination.unselectedIcon
                                            },
                                        contentDescription = stringResource(id = overloadDestination.iconTextId),
                                    )
                                },
                                label = {
                                    val label =
                                        when (overloadDestination.route) {
                                            "Home" -> {
                                                stringResource(id = R.string.home)
                                            }

                                            "Calendar" -> {
                                                stringResource(id = R.string.calendar)
                                            }

                                            "Configurations" -> {
                                                stringResource(id = R.string.configurations)
                                            }

                                            else -> {
                                                stringResource(id = R.string.unknown_day)
                                            }
                                        }
                                    TextView(
                                        text = label,
                                        fontWeight =
                                            if (selectedDestination == overloadDestination.route) {
                                                FontWeight.Bold
                                            } else {
                                                FontWeight.Normal
                                            },
                                    )
                                },
                            )
                        }
                    }
                }

                BottomBarState.Deleting -> {
                    HomeTabDeleteBottomAppBar(categoryState, itemState, itemEvent)
                }

                BottomBarState.Category -> {
                    CategoryScreenBottomAppBar(categoryState, categoryEvent, itemState, itemEvent, navController)
                }

                BottomBarState.Day -> {
                    DayScreenBottomAppBar(navController)
                }
            }
        }
    }
}

class TopBarState private constructor() {
    companion object {
        val Home = TopBarState()
        val Calendar = TopBarState()
        val Configurations = TopBarState()

        val Category = TopBarState()
        val Day = TopBarState()

        val Deleting = TopBarState()
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun OverloadTopAppBar(
    selectedDestination: String,
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    var currentTopBarState by remember { mutableStateOf(TopBarState.Home) }

    DisposableEffect(itemState.isDeletingHome, selectedDestination) {
        currentTopBarState =
            when (selectedDestination) {
                OverloadRoute.HOME -> {
                    when (itemState.isDeletingHome) {
                        true -> TopBarState.Deleting
                        false -> TopBarState.Home
                    }
                }

                OverloadRoute.CALENDAR -> {
                    when (itemState.isDeletingHome) {
                        true -> TopBarState.Deleting
                        false -> TopBarState.Calendar
                    }
                }

                OverloadRoute.CATEGORY -> {
                    TopBarState.Category
                }

                OverloadRoute.CONFIGURATIONS -> {
                    TopBarState.Configurations
                }

                OverloadRoute.DAY -> {
                    when (itemState.isDeletingHome) {
                        true -> TopBarState.Deleting
                        false -> TopBarState.Day
                    }
                }

                else -> TopBarState.Home
            }
        onDispose {
            currentTopBarState = TopBarState.Home
        }
    }

    for (topBarState in listOf(
        TopBarState.Home,
        TopBarState.Calendar,
        TopBarState.Configurations,
    )) {
        if (topBarState == currentTopBarState) {
            when (topBarState) {
                TopBarState.Home -> {
                    HomeTabTopAppBar(categoryState, categoryEvent)
                }

                TopBarState.Calendar -> {
                    CalendarTabTopAppBar(categoryState, categoryEvent, itemState, itemEvent)
                }

                TopBarState.Configurations -> {
                    ConfigurationsTabTopAppBar()
                }
            }
        }
    }

    for (topBarState in listOf(
        TopBarState.Category,
        TopBarState.Day,
        TopBarState.Deleting,
    )) {
        AnimatedVisibility(
            visible = topBarState == currentTopBarState,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
        ) {
            when (topBarState) {
                TopBarState.Category -> {
                    CategoryScreenTopAppBar(categoryState)
                }

                TopBarState.Day -> {
                    DayScreenTopAppBar(itemState)
                }

                TopBarState.Deleting -> {
                    DeleteTopAppBar(itemState, itemEvent)
                }
            }
        }
    }
}

@Composable
fun ModalNavigationDrawerContent(
    selectedDestination: String,
    navigationContentPosition: OverloadNavigationContentPosition,
    navigateToTopLevelDestination: (OverloadTopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {},
    categoryEvent: (CategoryEvent) -> Unit,
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    ModalDrawerSheet {
        Layout(
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
                    .padding(16.dp),
            content = {
                Column(
                    modifier = Modifier.layoutId(LayoutType.HEADER).padding(bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextView(
                            text = stringResource(id = R.string.app_name),
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        )
                        IconButton(onClick = onDrawerClicked) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuOpen,
                                contentDescription = stringResource(id = R.string.navigation_drawer),
                            )
                        }
                    }

                    OverloadNavigationFab(categoryEvent, categoryState, itemState, itemEvent, onDrawerClicked)
                }

                Column(modifier = Modifier.layoutId(LayoutType.CONTENT)) {
                    when (itemState.isDeletingHome) {
                        true -> {
                            val date = getSelectedDay(itemState)
                            val items = getItems(categoryState, itemState, date)

                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                NavigationDrawerItem(
                                    selected = false,
                                    onClick = {
                                        itemEvent(
                                            ItemEvent.SetSelectedItemsHome(
                                                itemState.selectedItemsHome +
                                                    items.filterNot {
                                                        itemState.selectedItemsHome.contains(
                                                            it,
                                                        )
                                                    },
                                            ),
                                        )
                                    },
                                    label = {
                                        TextView(
                                            stringResource(
                                                id = R.string.select_all_items_of_selected_day,
                                            ).replaceFirstChar { it.uppercase() },
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Filled.SelectAll,
                                            contentDescription = stringResource(id = R.string.select_all_items_of_selected_day),
                                        )
                                    },
                                    colors =
                                        NavigationDrawerItemDefaults.colors(
                                            unselectedContainerColor = Color.Transparent,
                                        ),
                                )
                                NavigationDrawerItem(
                                    selected = false,
                                    onClick = {
                                        itemEvent(ItemEvent.SetSelectedItemsHome(itemState.selectedItemsHome - items.toSet()))
                                    },
                                    label = {
                                        TextView(
                                            stringResource(
                                                id = R.string.deselect_all_items_of_selected_day,
                                            ).replaceFirstChar { it.uppercase() },
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Filled.Deselect,
                                            contentDescription = stringResource(id = R.string.deselect_all_items_of_selected_day),
                                        )
                                    },
                                    colors =
                                        NavigationDrawerItemDefaults.colors(
                                            unselectedContainerColor = Color.Transparent,
                                        ),
                                )
                            }
                        }
                        false -> {
                            AnimatedVisibility(visible = itemState.isFabOpen.not()) {
                                Column(
                                    modifier = Modifier.verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    TOP_LEVEL_DESTINATIONS.forEach { overloadDestination ->
                                        NavigationDrawerItem(
                                            selected = selectedDestination == overloadDestination.route,
                                            label = {
                                                TextView(stringResource(id = overloadDestination.iconTextId))
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector =
                                                        if (selectedDestination == overloadDestination.route) {
                                                            overloadDestination.selectedIcon
                                                        } else {
                                                            overloadDestination.unselectedIcon
                                                        },
                                                    contentDescription =
                                                        stringResource(
                                                            id = overloadDestination.iconTextId,
                                                        ),
                                                )
                                            },
                                            colors =
                                                NavigationDrawerItemDefaults.colors(
                                                    unselectedContainerColor = Color.Transparent,
                                                ),
                                            onClick = {
                                                navigateToTopLevelDestination(
                                                    overloadDestination,
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            measurePolicy = navigationMeasurePolicy(navigationContentPosition),
        )
    }
}

fun navigationMeasurePolicy(navigationContentPosition: OverloadNavigationContentPosition): MeasurePolicy {
    return MeasurePolicy { measurables, constraints ->
        lateinit var headerMeasurable: Measurable
        lateinit var contentMeasurable: Measurable
        measurables.forEach {
            when (it.layoutId) {
                LayoutType.HEADER -> headerMeasurable = it
                LayoutType.CONTENT -> contentMeasurable = it
                else -> error("Unknown layoutId encountered!")
            }
        }

        val headerPlaceable = headerMeasurable.measure(constraints)
        val contentPlaceable =
            contentMeasurable.measure(
                constraints.offset(vertical = -headerPlaceable.height),
            )
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Place the header, this goes at the top
            headerPlaceable.placeRelative(0, 0)

            // Determine how much space is not taken up by the content
            val nonContentVerticalSpace = constraints.maxHeight - contentPlaceable.height

            val contentPlaceableY =
                when (navigationContentPosition) {
                    // Figure out the place we want to place the content, with respect to the
                    // parent (ignoring the header for now)
                    OverloadNavigationContentPosition.TOP -> 0
                    OverloadNavigationContentPosition.CENTER -> nonContentVerticalSpace / 2
                }
                    // And finally, make sure we don't overlap with the header.
                    .coerceAtLeast(headerPlaceable.height)

            contentPlaceable.placeRelative(0, contentPlaceableY)
        }
    }
}

enum class LayoutType {
    HEADER,
    CONTENT,
}
