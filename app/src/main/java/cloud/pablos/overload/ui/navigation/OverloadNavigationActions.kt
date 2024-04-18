package cloud.pablos.overload.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import cloud.pablos.overload.R

object OverloadRoute {
    const val HOME = "Home"
    const val CALENDAR = "Calendar"
    const val CATEGORY = "Category"
    const val DAY = "Day"
    const val CONFIGURATIONS = "Configurations"
}

data class OverloadTopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int,
)

class OverloadNavigationActions(private val navController: NavHostController) {
    fun navigateTo(destination: OverloadTopLevelDestination) {
        navController.navigate(destination.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}

val TOP_LEVEL_DESTINATIONS =
    listOf(
        OverloadTopLevelDestination(
            OverloadRoute.HOME,
            Icons.Filled.CalendarToday,
            Icons.Outlined.CalendarToday,
            R.string.home,
        ),
        OverloadTopLevelDestination(
            OverloadRoute.CALENDAR,
            Icons.Filled.CalendarMonth,
            Icons.Outlined.CalendarMonth,
            R.string.calendar,
        ),
        OverloadTopLevelDestination(
            OverloadRoute.CONFIGURATIONS,
            Icons.Filled.Settings,
            Icons.Outlined.Settings,
            R.string.configurations,
        ),
    )
