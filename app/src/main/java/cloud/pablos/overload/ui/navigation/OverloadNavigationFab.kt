package cloud.pablos.overload.ui.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
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
import androidx.compose.ui.Modifier
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
import cloud.pablos.overload.ui.tabs.home.HomeTabManualDialog
import cloud.pablos.overload.ui.views.TextView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@Composable
fun OverloadNavigationFab(
    categoryEvent: (CategoryEvent) -> Unit,
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
    onDrawerClicked: () -> Unit = {},
) {
    val backgroundColor = decideBackground(categoryState)
    val foregroundColor = decideForeground(backgroundColor)

    val date = LocalDate.now()

    val itemsForToday = getItems(categoryState, itemState, date)

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
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (itemState.isFabOpen) {
            true -> {
                FloatingActionButton(
                    {
                        itemEvent(ItemEvent.SetIsFabOpen(false))
                    },
                    modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    interactionSource = interactionSource,
                ) {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Row(
                            Modifier.padding(8.dp),
                            Arrangement.Start,
                            Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Default.Close,
                                stringResource(R.string.close),
                                Modifier.padding(8.dp),
                            )

                            TextView(
                                stringResource(R.string.close),
                                Modifier.padding(end = 8.dp),
                            )
                        }
                    }
                }

                SmallFloatingActionButton(
                    {
                        itemEvent(ItemEvent.SetIsFabOpen(false))
                        manualDialogState.value = true
                        onDrawerClicked()
                    },
                    Modifier.padding(bottom = 10.dp).fillMaxWidth(),
                    containerColor = backgroundColor,
                    contentColor = foregroundColor,
                ) {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Row(
                            Modifier.padding(8.dp),
                            Arrangement.Start,
                            Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Default.Add,
                                stringResource(R.string.manual_entry),
                                Modifier.padding(8.dp),
                            )

                            TextView(
                                stringResource(R.string.manual_entry),
                                Modifier.padding(end = 8.dp),
                            )
                        }
                    }
                }

                if (categoryState.categories.count() > 1) {
                    SmallFloatingActionButton(
                        {
                            itemEvent(ItemEvent.SetIsFabOpen(false))
                            categoryEvent(CategoryEvent.SetIsSwitchCategoryDialogOpenHome(true))
                            onDrawerClicked()
                        },
                        Modifier.padding(bottom = 10.dp).fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ) {
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Row(
                                Modifier.padding(8.dp),
                                Arrangement.Start,
                                Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Default.Category,
                                    stringResource(R.string.switch_category),
                                    Modifier.padding(8.dp),
                                )

                                TextView(
                                    stringResource(R.string.switch_category),
                                    Modifier.padding(end = 8.dp),
                                )
                            }
                        }
                    }
                }
            }
            false -> {
                when (itemState.isDeletingHome) {
                    true -> {
                        FloatingActionButton(
                            {
                                itemEvent(ItemEvent.DeleteItems(itemState.selectedItemsHome))
                            },
                            Modifier.fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            interactionSource = interactionSource,
                        ) {
                            Column(
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start,
                            ) {
                                Row(
                                    Modifier.padding(8.dp),
                                    Arrangement.Start,
                                    Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        Icons.Filled.DeleteForever,
                                        stringResource(R.string.delete_items_forever),
                                        Modifier.padding(8.dp),
                                    )

                                    TextView(
                                        stringResource(R.string.delete_items_forever).replaceFirstChar { it.uppercase() },
                                        Modifier.padding(end = 8.dp),
                                    )
                                }
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
                            Modifier.fillMaxWidth(),
                            containerColor = backgroundColor,
                            contentColor = foregroundColor,
                            interactionSource = interactionSource,
                        ) {
                            Column(
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start,
                            ) {
                                Row(
                                    Modifier.padding(8.dp),
                                    Arrangement.Start,
                                    Alignment.CenterVertically,
                                ) {
                                    if (isOngoing) {
                                        Icon(
                                            Icons.Default.Stop,
                                            stringResource(R.string.stop),
                                            Modifier.padding(8.dp),
                                        )

                                        TextView(
                                            stringResource(R.string.stop),
                                            Modifier.padding(end = 8.dp),
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.PlayArrow,
                                            stringResource(R.string.start),
                                            Modifier.padding(8.dp),
                                        )

                                        TextView(
                                            stringResource(R.string.start),
                                            Modifier.padding(end = 8.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (manualDialogState.value) {
        HomeTabManualDialog({ manualDialogState.value = false }, categoryState, itemState, itemEvent)
    }
}
