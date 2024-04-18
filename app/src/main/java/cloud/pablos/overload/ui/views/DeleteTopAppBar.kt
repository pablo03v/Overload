package cloud.pablos.overload.ui.views

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cloud.pablos.overload.R
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteTopAppBar(
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    BackHandler {
        itemEvent(ItemEvent.SetIsDeletingHome(false))
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = NavigationBarDefaults.Elevation,
    ) {
        TopAppBar(
            {
                TextView(
                    itemState.selectedItemsHome.size.toString() + " " + stringResource(R.string.ol_selected),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            },
            actions = {
                IconButton({
                    itemEvent(ItemEvent.SetIsDeletingHome(false))
                }) {
                    Icon(
                        Icons.Filled.Close,
                        stringResource(R.string.get_out_of_isDeleting),
                        tint = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primaryContainer,
                ),
        )
    }
}
