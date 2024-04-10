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
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.data.item.startOrStopPause
import cloud.pablos.overload.ui.tabs.home.HomeTabManualDialog
import cloud.pablos.overload.ui.tabs.home.getItemsOfDay
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
                    interactionSource = interactionSource,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.close),
                                modifier = Modifier.padding(8.dp),
                            )

                            TextView(
                                text = stringResource(id = R.string.close),
                                modifier = Modifier.padding(end = 8.dp),
                            )
                        }
                    }
                }

                SmallFloatingActionButton(
                    onClick = {
                        itemEvent(ItemEvent.SetIsFabOpen(false))
                        manualDialogState.value = true
                        onDrawerClicked()
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = R.string.close),
                                modifier = Modifier.padding(8.dp),
                            )

                            TextView(
                                text = stringResource(id = R.string.manual_entry),
                                modifier = Modifier.padding(end = 8.dp),
                            )
                        }
                    }
                }

                categoryState.categories.forEach { category ->
                    SmallFloatingActionButton(
                        onClick = {
                            categoryEvent(CategoryEvent.SetSelectedCategory(category.id))

                            itemEvent(ItemEvent.SetIsFabOpen(false))

                            onDrawerClicked()
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.padding(8.dp),
                            ) {
                                /*Icon(
                                    imageVector = category.icon,
                                    contentDescription = category.name,
                                    modifier = Modifier.padding(8.dp),
                                )*/

                                TextView(
                                    text = "Switch to " + category.name,
                                    modifier = Modifier.padding(end = 8.dp),
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
                            onClick = {
                                itemEvent(ItemEvent.DeleteItems(itemState.selectedItemsHome))
                            },
                            interactionSource = interactionSource,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Icon(
                                        Icons.Filled.DeleteForever,
                                        contentDescription = stringResource(id = R.string.delete_items_forever),
                                        modifier = Modifier.padding(8.dp),
                                    )

                                    TextView(
                                        text = stringResource(id = R.string.delete_items_forever).replaceFirstChar { it.uppercase() },
                                        modifier = Modifier.padding(end = 8.dp),
                                    )
                                }
                            }
                        }
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
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Icon(
                                        imageVector = if (isOngoing) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription =
                                            if (isOngoing) {
                                                stringResource(id = R.string.stop)
                                            } else {
                                                stringResource(
                                                    id = R.string.start,
                                                )
                                            },
                                        modifier = Modifier.padding(8.dp),
                                    )

                                    TextView(
                                        text =
                                            if (isOngoing) {
                                                stringResource(id = R.string.stop)
                                            } else {
                                                stringResource(
                                                    id = R.string.start,
                                                )
                                            },
                                        modifier = Modifier.padding(end = 8.dp),
                                    )
                                }
                            }
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
