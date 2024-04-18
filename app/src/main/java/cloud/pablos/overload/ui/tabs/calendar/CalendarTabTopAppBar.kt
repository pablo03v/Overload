package cloud.pablos.overload.ui.tabs.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cloud.pablos.overload.R
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.views.ChangeYearButton
import cloud.pablos.overload.ui.views.SwitchCategoryButton
import cloud.pablos.overload.ui.views.TextView

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTabTopAppBar(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = NavigationBarDefaults.Elevation,
    ) {
        TopAppBar(
            {
                TextView(
                    stringResource(R.string.calendar),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            },
            actions = {
                ChangeYearButton(itemState, itemEvent)
                SwitchCategoryButton(categoryState, categoryEvent)
            },
        )
    }
}
