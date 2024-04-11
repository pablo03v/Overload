package cloud.pablos.overload.ui.views

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import cloud.pablos.overload.R
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.Item
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AdjustEndDialog(
    onClose: () -> Unit,
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val context = LocalContext.current
    val learnMoreLink = "https://codeberg.org/pabloscloud/Overload#spread-acorss-days".toUri()

    val date = LocalDate.now()

    val itemsNotToday =
        itemState.items.filter { item ->
            val startTime = parseToLocalDateTime(item.startTime)
            extractDate(startTime) != date
        }
    val isOngoingNotToday = itemsNotToday.isNotEmpty() && itemsNotToday.any { it.ongoing }
    val firstOngoingItem = itemsNotToday.find { it.ongoing }

    var selectedTimeText by remember { mutableStateOf("") }

    if (isOngoingNotToday && firstOngoingItem != null) {
        val startTime = parseToLocalDateTime(firstOngoingItem.startTime)
        var endTime by remember { mutableStateOf(startTime) }

        val timePicker =
            TimePickerDialog(
                context,
                { _, selectedHour: Int, selectedMinute: Int ->
                    val newEndTime = endTime.withHour(selectedHour).withMinute(selectedMinute)
                    if (newEndTime.isAfter(startTime)) {
                        endTime = newEndTime
                        selectedTimeText = "$selectedHour:$selectedMinute"
                    } else {
                        endTime = startTime.plusMinutes(1)
                    }
                },
                endTime.hour,
                endTime.minute,
                false,
            )

        AlertDialog(
            onDismissRequest = onClose,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = stringResource(R.string.adjust_end_time),
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            title = {
                TextView(
                    text = stringResource(R.string.adjust_end_time),
                    fontWeight = FontWeight.Bold,
                    align = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.adjust_descr),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val openLinkStr = stringResource(id = R.string.open_link_with)
                    ClickableText(
                        text = AnnotatedString(stringResource(id = R.string.learn_more)),
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                            ),
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, learnMoreLink)
                            val chooserIntent = Intent.createChooser(intent, openLinkStr)
                            ContextCompat.startActivity(context, chooserIntent, null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DayViewItemOngoing(
                        item = firstOngoingItem,
                        showDate = true,
                        hideEnd = true,
                        categoryState = categoryState,
                        itemState = itemState,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextView(endTime.format(DateTimeFormatter.ofPattern("MM/dd/yy HH:mm")))
                        Button(
                            onClick = {
                                timePicker.show()
                            },
                            colors =
                                ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                        ) { TextView(stringResource(R.string.adjust)) }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClose.save(itemEvent, firstOngoingItem, endTime)
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                ) {
                    TextView(stringResource(R.string.save))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onClose()
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                ) {
                    TextView(stringResource(id = R.string.close))
                }
            },
            modifier = Modifier.padding(16.dp),
        )
    } else {
        onClose()
    }
}

private fun (() -> Unit).save(
    itemEvent: (ItemEvent) -> Unit,
    item: Item,
    newEnd: LocalDateTime,
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

    itemEvent(ItemEvent.SetId(item.id))
    itemEvent(ItemEvent.SetStart(item.startTime))
    itemEvent(ItemEvent.SetEnd(newEnd.format(formatter)))
    itemEvent(ItemEvent.SetOngoing(false))
    itemEvent(ItemEvent.SetPause(item.pause))
    itemEvent(ItemEvent.SaveItem)

    this()
}
