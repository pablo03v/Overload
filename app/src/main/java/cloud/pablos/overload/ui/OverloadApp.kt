package cloud.pablos.overload.ui

import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.navigation.ModalNavigationDrawerContent
import cloud.pablos.overload.ui.navigation.OverloadBottomNavigationBar
import cloud.pablos.overload.ui.navigation.OverloadNavigationActions
import cloud.pablos.overload.ui.navigation.OverloadNavigationRail
import cloud.pablos.overload.ui.navigation.OverloadRoute
import cloud.pablos.overload.ui.navigation.OverloadTopLevelDestination
import cloud.pablos.overload.ui.screens.day.DayScreen
import cloud.pablos.overload.ui.tabs.calendar.CalendarTab
import cloud.pablos.overload.ui.tabs.configurations.ConfigurationsTab
import cloud.pablos.overload.ui.tabs.home.HomeTab
import cloud.pablos.overload.ui.utils.DevicePosture
import cloud.pablos.overload.ui.utils.OverloadContentType
import cloud.pablos.overload.ui.utils.OverloadNavigationContentPosition
import cloud.pablos.overload.ui.utils.OverloadNavigationType
import cloud.pablos.overload.ui.utils.isBookPosture
import cloud.pablos.overload.ui.utils.isSeparating
import cloud.pablos.overload.ui.views.AdjustEndDialog
import cloud.pablos.overload.ui.views.ForgotToStopDialog
import cloud.pablos.overload.ui.views.SpreadAcrossDaysDialog
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun OverloadApp(
    windowSize: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    state: ItemState,
    onEvent: (ItemEvent) -> Unit,
    filePickerLauncher: ActivityResultLauncher<Intent>,
) {
    val navigationType: OverloadNavigationType
    val contentType: OverloadContentType

    val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()

    val foldingDevicePosture =
        when {
            isBookPosture(foldingFeature) ->
                DevicePosture.BookPosture(foldingFeature.bounds)

            isSeparating(foldingFeature) ->
                DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)

            else -> DevicePosture.NormalPosture
        }

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            navigationType = OverloadNavigationType.BOTTOM_NAVIGATION
            contentType = OverloadContentType.SINGLE_PANE
        }

        WindowWidthSizeClass.Medium -> {
            navigationType =
                if (foldingDevicePosture is DevicePosture.NormalPosture) {
                    OverloadNavigationType.BOTTOM_NAVIGATION
                } else {
                    OverloadNavigationType.NAVIGATION_RAIL
                }
            contentType =
                if (foldingDevicePosture is DevicePosture.NormalPosture) {
                    OverloadContentType.DUAL_PANE
                } else {
                    OverloadContentType.SINGLE_PANE
                }
        }

        WindowWidthSizeClass.Expanded -> {
            navigationType = OverloadNavigationType.NAVIGATION_RAIL
            contentType = OverloadContentType.DUAL_PANE
        }

        else -> {
            navigationType = OverloadNavigationType.BOTTOM_NAVIGATION
            contentType = OverloadContentType.SINGLE_PANE
        }
    }

    val navigationContentPosition =
        when (windowSize.heightSizeClass) {
            WindowHeightSizeClass.Compact -> {
                OverloadNavigationContentPosition.TOP
            }

            WindowHeightSizeClass.Medium,
            WindowHeightSizeClass.Expanded,
            -> {
                OverloadNavigationContentPosition.TOP
            }

            else -> {
                OverloadNavigationContentPosition.TOP
            }
        }

    OverloadNavigationWrapper(
        navigationType = navigationType,
        contentType = contentType,
        navigationContentPosition = navigationContentPosition,
        state = state,
        onEvent = onEvent,
        filePickerLauncher = filePickerLauncher,
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun OverloadNavigationWrapper(
    navigationType: OverloadNavigationType,
    contentType: OverloadContentType,
    navigationContentPosition: OverloadNavigationContentPosition,
    state: ItemState,
    onEvent: (ItemEvent) -> Unit,
    filePickerLauncher: ActivityResultLauncher<Intent>,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navigationActions =
        remember(navController) {
            OverloadNavigationActions(navController)
        }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination =
        navBackStackEntry?.destination?.route ?: OverloadRoute.HOME

    when (navigationType) {
        OverloadNavigationType.BOTTOM_NAVIGATION -> {
            OverloadAppContent(
                navigationType = navigationType,
                contentType = contentType,
                navigationContentPosition = navigationContentPosition,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationActions::navigateTo,
                onDrawerClicked = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                state = state,
                onEvent = onEvent,
                filePickerLauncher = filePickerLauncher,
            )
        }

        OverloadNavigationType.NAVIGATION_RAIL -> {
            ModalNavigationDrawer(
                drawerContent = {
                    ModalNavigationDrawerContent(
                        selectedDestination = selectedDestination,
                        navigationContentPosition = navigationContentPosition,
                        navigateToTopLevelDestination = navigationActions::navigateTo,
                        onDrawerClicked = {
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        state = state,
                        onEvent = onEvent,
                    )
                },
                drawerState = drawerState,
            ) {
                OverloadAppContent(
                    navigationType = navigationType,
                    contentType = contentType,
                    navigationContentPosition = navigationContentPosition,
                    navController = navController,
                    selectedDestination = selectedDestination,
                    navigateToTopLevelDestination = navigationActions::navigateTo,
                    onDrawerClicked = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    state = state,
                    onEvent = onEvent,
                    filePickerLauncher = filePickerLauncher,
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun OverloadAppContent(
    modifier: Modifier = Modifier,
    navigationType: OverloadNavigationType,
    contentType: OverloadContentType,
    navigationContentPosition: OverloadNavigationContentPosition,
    navController: NavHostController,
    selectedDestination: String,
    navigateToTopLevelDestination: (OverloadTopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {},
    state: ItemState,
    onEvent: (ItemEvent) -> Unit,
    filePickerLauncher: ActivityResultLauncher<Intent>,
) {
    var forgotDialogState by remember { mutableStateOf(false) }
    LaunchedEffect(state.isForgotToStopDialogShown) {
        forgotDialogState = state.isForgotToStopDialogShown
    }

    var adjustEndDialogState by remember { mutableStateOf(false) }
    LaunchedEffect(state.isAdjustEndDialogShown) {
        adjustEndDialogState = state.isAdjustEndDialogShown
    }

    var spreadAcrossDaysDialogState by remember { mutableStateOf(false) }
    LaunchedEffect(state.isSpreadAcrossDaysDialogShown) {
        spreadAcrossDaysDialogState = state.isSpreadAcrossDaysDialogShown
    }

    Row(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == OverloadNavigationType.NAVIGATION_RAIL) {
            OverloadNavigationRail(
                selectedDestination = selectedDestination,
                navigationContentPosition = navigationContentPosition,
                navigateToTopLevelDestination = navigateToTopLevelDestination,
                onDrawerClicked = onDrawerClicked,
                state = state,
                onEvent = onEvent,
            )
        }
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
        ) {
            OverloadNavHost(
                navigationType = navigationType,
                contentType = contentType,
                navController = navController,
                state = state,
                onEvent = onEvent,
                filePickerLauncher = filePickerLauncher,
                modifier =
                    Modifier
                        .weight(1f)
                        .then(
                            if (navigationType == OverloadNavigationType.BOTTOM_NAVIGATION && state.isDeletingHome.not()) {
                                Modifier.consumeWindowInsets(
                                    WindowInsets.systemBars.only(
                                        WindowInsetsSides.Bottom,
                                    ),
                                )
                            } else {
                                Modifier
                            },
                        ),
            )
            AnimatedVisibility(visible = navigationType == OverloadNavigationType.BOTTOM_NAVIGATION) {
                OverloadBottomNavigationBar(
                    selectedDestination = selectedDestination,
                    navigateToTopLevelDestination = navigateToTopLevelDestination,
                    state = state,
                    onEvent = onEvent,
                    onNavigate = { navController.navigate(OverloadRoute.CALENDAR) },
                )
            }
        }
    }
    if (forgotDialogState) {
        ForgotToStopDialog(
            onClose = { onEvent(ItemEvent.SetForgotToStopDialogShown(false)) },
            onEvent,
        )
    }

    if (adjustEndDialogState) {
        AdjustEndDialog(
            onClose = { onEvent(ItemEvent.SetAdjustEndDialogShown(false)) },
            state,
            onEvent,
        )
    }

    if (spreadAcrossDaysDialogState) {
        SpreadAcrossDaysDialog(
            onClose = { onEvent(ItemEvent.SetSpreadAcrossDaysDialogShown(false)) },
            state,
            onEvent,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun OverloadNavHost(
    navigationType: OverloadNavigationType,
    contentType: OverloadContentType,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    state: ItemState,
    onEvent: (ItemEvent) -> Unit,
    filePickerLauncher: ActivityResultLauncher<Intent>,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = OverloadRoute.HOME,
    ) {
        composable(OverloadRoute.HOME) {
            HomeTab(
                navigationType = navigationType,
                state = state,
                onEvent = onEvent,
            )
        }
        composable(OverloadRoute.CALENDAR) {
            CalendarTab(
                contentType = contentType,
                state = state,
                onEvent = onEvent,
                onNavigate = { navController.navigate(OverloadRoute.DAY) },
            )
        }
        composable(OverloadRoute.DAY) {
            DayScreen(
                state = state,
                onEvent = onEvent,
            )
        }
        composable(OverloadRoute.CONFIGURATIONS) {
            ConfigurationsTab(
                state = state,
                onEvent = onEvent,
                filePickerLauncher = filePickerLauncher,
            )
        }
    }
}
