package cloud.pablos.overload.ui.screens.day

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import cloud.pablos.overload.R
import cloud.pablos.overload.ui.navigation.OverloadRoute

@Composable
fun DayScreenBottomAppBar(navController: NavHostController) {
    BottomAppBar(
        {
            IconButton({ navController.navigate(OverloadRoute.CALENDAR) }) {
                Icon(
                    Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    stringResource(R.string.go_back),
                )
            }
        },
    )
}
