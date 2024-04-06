package cloud.pablos.overload.ui.tabs.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.rounded.Work
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.data.item.startOrStopPause
import cloud.pablos.overload.ui.views.TextView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeTabFab(
    state: ItemState,
    onEvent: (ItemEvent) -> Unit,
) {
    val date = LocalDate.now()

    val itemsForToday = getItemsOfDay(date, state)

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

                    onEvent(ItemEvent.SetIsFabOpen(true))
                    onEvent(ItemEvent.SetIsDeletingHome(false))
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
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (state.isFabOpen) {
            true -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        TextView(
                            text = "Switch to Work",
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }

                    SmallFloatingActionButton(
                        onClick = {
                            onEvent(ItemEvent.SetIsFabOpen(false))

                            // TODO: switch to category
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Work,
                            contentDescription = "Work,",
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        TextView(
                            text = stringResource(id = R.string.manual_entry),
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }

                    SmallFloatingActionButton(
                        onClick = {
                            onEvent(ItemEvent.SetIsFabOpen(false))
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
                }

                FloatingActionButton(
                    onClick = {
                        onEvent(ItemEvent.SetIsFabOpen(false))
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.close),
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }

            false -> {
                FloatingActionButton(
                    onClick = {
                        if (isLongClick.not()) {
                            startOrStopPause(state, onEvent)
                        }
                    },
                    interactionSource = interactionSource,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
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

    if (manualDialogState.value) {
        HomeTabManualDialog(onClose = { manualDialogState.value = false }, state, onEvent)
    }
}
