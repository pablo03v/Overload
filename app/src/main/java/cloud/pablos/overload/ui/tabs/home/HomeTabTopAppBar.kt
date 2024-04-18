package cloud.pablos.overload.ui.tabs.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cloud.pablos.overload.R
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.ui.views.SwitchCategoryButton
import cloud.pablos.overload.ui.views.TextView

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTabTopAppBar(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = NavigationBarDefaults.Elevation,
    ) {
        TopAppBar(
            {
                TextView(
                    stringResource(R.string.home),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            },
            actions = {
                SwitchCategoryButton(categoryState, categoryEvent)
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
        )
    }
}
