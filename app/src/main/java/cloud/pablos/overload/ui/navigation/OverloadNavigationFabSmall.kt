package cloud.pablos.overload.ui.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.data.item.startOrStopPause
import cloud.pablos.overload.ui.tabs.home.HomeTabDeleteFAB
import cloud.pablos.overload.ui.tabs.home.HomeTabManualDialog
import cloud.pablos.overload.ui.tabs.home.getItemsOfDay
import cloud.pablos.overload.ui.views.TextView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@Composable
fun OverloadNavigationFabSmall(
    categoryEvent: (CategoryEvent) -> Unit,
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val date = LocalDate.now()

    val itemsForToday = getItemsOfDay(date, categoryState, itemState)

    val isOngoing = itemsForToday.isNotEmpty() && itemsForToday.last().ongoing

    val interactionSource = remember { MutableInteractionSource() }

    val viewConfiguration = LocalViewConfiguration.current

    var isLongClick by remember { mutableStateOf(false) }

    val manualDialogState = remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    isLongClick = false
                    delay(viewConfiguration.longPressTimeoutMillis)
                    isLongClick = true

                    itemEvent(ItemEvent.SetIsFabOpen(true))
                    itemEvent(ItemEvent.SetIsDeletingHome(false))
                }
                is PressInteraction.Release -> {
                }
                is PressInteraction.Cancel -> {
                    isLongClick = false
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        when (itemState.isFabOpen) {
            true -> {
                FloatingActionButton(
                    onClick = {
                        itemEvent(ItemEvent.SetIsFabOpen(false))
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.close),
                    )
                }

                SmallFloatingActionButton(
                    onClick = {
                        itemEvent(ItemEvent.SetIsFabOpen(false))
                        manualDialogState.value = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.manual_entry),
                    )
                }

                categoryState.categories.forEach { category ->
                    SmallFloatingActionButton(
                        onClick = {
                            categoryEvent(CategoryEvent.SetSelectedCategory(category.id))

                            itemEvent(ItemEvent.SetIsFabOpen(false))
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        TextView(text = category.emoji)
                    }
                }
            }
            false -> {
                when (itemState.isDeletingHome) {
                    true -> {
                        HomeTabDeleteFAB(itemState, itemEvent)
                    }

                    false -> {
                        FloatingActionButton(
                            onClick = {
                                if (isLongClick.not()) {
                                    startOrStopPause(categoryState, itemState, itemEvent)
                                }
                            },
                            interactionSource = interactionSource,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ) {
                            Icon(
                                imageVector =
                                    if (isOngoing) {
                                        Icons.Default.Stop
                                    } else {
                                        Icons.Default.PlayArrow
                                    },
                                contentDescription =
                                    if (isOngoing) {
                                        stringResource(id = R.string.stop)
                                    } else {
                                        stringResource(id = R.string.start)
                                    },
                            )
                        }
                    }
                }
            }
        }
    }

    if (manualDialogState.value) {
        HomeTabManualDialog(onClose = { manualDialogState.value = false }, categoryState, itemState, itemEvent)
    }
}
