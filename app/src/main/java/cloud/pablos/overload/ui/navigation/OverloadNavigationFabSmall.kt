package cloud.pablos.overload.ui.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
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
import cloud.pablos.overload.data.Helpers.Companion.decideBackground
import cloud.pablos.overload.data.Helpers.Companion.decideForeground
import cloud.pablos.overload.data.Helpers.Companion.getItems
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.data.item.fabPress
import cloud.pablos.overload.ui.tabs.home.HomeTabDeleteFAB
import cloud.pablos.overload.ui.views.AddEntryDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@Composable
fun OverloadNavigationFabSmall(
    selectedDestination: String,
    categoryEvent: (CategoryEvent) -> Unit,
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val backgroundColor = decideBackground(categoryState)
    val foregroundColor = decideForeground(backgroundColor)

    val addEntryDialogState = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (itemState.isDeletingHome) {
            true -> {
                HomeTabDeleteFAB(itemState, itemEvent)
            }

            false -> {
                when (selectedDestination) {
                    OverloadRoute.HOME -> {
                        val date = LocalDate.now()
                        val itemsForToday = getItems(categoryState, itemState, date)
                        val isOngoing = itemsForToday.isNotEmpty() && itemsForToday.last().ongoing
                        val interactionSource = remember { MutableInteractionSource() }
                        val viewConfiguration = LocalViewConfiguration.current
                        var isLongClick by remember { mutableStateOf(false) }

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

                        when (itemState.isFabOpen) {
                            true -> {
                                FloatingActionButton(
                                    {
                                        itemEvent(ItemEvent.SetIsFabOpen(false))
                                    },
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        stringResource(R.string.close),
                                    )
                                }

                                SmallFloatingActionButton(
                                    {
                                        itemEvent(ItemEvent.SetIsFabOpen(false))
                                        addEntryDialogState.value = true
                                    },
                                    containerColor = backgroundColor,
                                    contentColor = foregroundColor,
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        stringResource(R.string.manual_entry),
                                    )
                                }

                                if (categoryState.categories.count() > 1) {
                                    SmallFloatingActionButton(
                                        {
                                            itemEvent(ItemEvent.SetIsFabOpen(false))
                                            categoryEvent(CategoryEvent.SetIsSwitchCategoryDialogOpenHome(true))
                                        },
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ) {
                                        Icon(
                                            Icons.Default.Category,
                                            stringResource(R.string.switch_category),
                                        )
                                    }
                                }
                            }
                            false -> {
                                FloatingActionButton(
                                    {
                                        if (isLongClick.not()) {
                                            fabPress(categoryState, categoryEvent, itemState, itemEvent)
                                        }
                                    },
                                    containerColor = backgroundColor,
                                    contentColor = foregroundColor,
                                    interactionSource = interactionSource,
                                ) {
                                    if (isOngoing) {
                                        Icon(
                                            Icons.Default.Stop,
                                            stringResource(R.string.stop),
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.PlayArrow,
                                            stringResource(R.string.start),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    OverloadRoute.CALENDAR -> {
                        FloatingActionButton(
                            {
                                addEntryDialogState.value = true
                            },
                            containerColor = backgroundColor,
                            contentColor = foregroundColor,
                        ) {
                            Icon(
                                Icons.Default.Add,
                                stringResource(R.string.manual_entry),
                            )
                        }
                    }
                }
            }
        }
    }

    if (addEntryDialogState.value) {
        AddEntryDialog({ addEntryDialogState.value = false }, categoryState, itemState, itemEvent)
    }
}
