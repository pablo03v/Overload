package cloud.pablos.overload.ui.tabs.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.Helpers.Companion.decideBackground
import cloud.pablos.overload.data.Helpers.Companion.decideForeground
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.views.AddEntryDialog
import cloud.pablos.overload.ui.views.TextView
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CalendarTabFab(
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
    date: LocalDate = LocalDate.now(),
    extended: Boolean = true,
) {
    val backgroundColor = decideBackground(categoryState)
    val foregroundColor = decideForeground(backgroundColor)

    val addEntryDialogState = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        FloatingActionButton(
            {
                addEntryDialogState.value = true
            },
            containerColor = backgroundColor,
            contentColor = foregroundColor,
        ) {
            Row(
                Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.Add,
                    stringResource(R.string.manual_entry),
                    Modifier.padding(8.dp),
                )
                AnimatedVisibility(visible = extended) {
                    TextView(
                        stringResource(R.string.manual_entry),
                        Modifier.padding(end = 8.dp),
                    )
                }
            }
        }
    }

    if (addEntryDialogState.value) {
        AddEntryDialog({ addEntryDialogState.value = false }, categoryState, itemState, itemEvent)
    }
}
