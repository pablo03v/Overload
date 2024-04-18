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
import cloud.pablos.overload.data.Converters.Companion.convertStringToLocalDateTime
import cloud.pablos.overload.data.Helpers.Companion.decideBackground
import cloud.pablos.overload.data.Helpers.Companion.decideForeground
import cloud.pablos.overload.data.Helpers.Companion.getItemsPastDays
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.Item
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AdjustEndDialog(
    onDismiss: () -> Unit,
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val backgroundColor = decideBackground(categoryState)
    val foregroundColor = decideForeground(backgroundColor)

    val context = LocalContext.current
    val learnMoreLink = "https://github.com/pabloscloud/Overload?tab=readme-ov-file#why-does-the-app-annoy-me-with-a-popup-to-adjust-the-end".toUri()

    val itemsNotToday = getItemsPastDays(categoryState, itemState)
    val isOngoingNotToday = itemsNotToday.isNotEmpty() && itemsNotToday.any { it.ongoing }
    val firstOngoingItem = itemsNotToday.find { it.ongoing }

    var selectedTimeText by remember { mutableStateOf("") }

    if (isOngoingNotToday && firstOngoingItem != null) {
        val startTime = convertStringToLocalDateTime(firstOngoingItem.startTime)
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
            onDismiss,
            {
                Button(
                    onClick = {
                        onDismiss.save(itemEvent, firstOngoingItem, endTime)
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = backgroundColor,
                            contentColor = foregroundColor,
                        ),
                ) {
                    TextView(stringResource(R.string.save))
                }
            },
            Modifier.padding(16.dp),
            {
                Button(
                    { onDismiss() },
                    colors =
                        ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                ) {
                    TextView(stringResource(R.string.close))
                }
            },
            {
                Icon(
                    Icons.Rounded.Info,
                    stringResource(R.string.adjust_end_time),
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            {
                TextView(
                    stringResource(R.string.adjust_end_time),
                    Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    align = TextAlign.Center,
                    maxLines = 2,
                )
            },
            {
                Column {
                    Text(
                        stringResource(R.string.adjust_descr),
                        Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(16.dp))

                    val openLinkStr = stringResource(R.string.open_link_with)
                    ClickableText(
                        AnnotatedString(stringResource(R.string.learn_more)),
                        Modifier.fillMaxWidth(),
                        MaterialTheme.typography.bodyMedium.copy(
                            MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                        ),
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, learnMoreLink)
                            val chooserIntent = Intent.createChooser(intent, openLinkStr)
                            ContextCompat.startActivity(context, chooserIntent, null)
                        },
                    )

                    Spacer(Modifier.height(16.dp))

                    DayViewItemOngoing(
                        firstOngoingItem,
                        categoryState,
                        itemState,
                        showDate = true,
                        hideEnd = true,
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically,
                    ) {
                        TextView(endTime.format(DateTimeFormatter.ofPattern("MM/dd/yy HH:mm")))
                        Button(
                            { timePicker.show() },
                            colors =
                                ButtonColors(
                                    MaterialTheme.colorScheme.tertiaryContainer,
                                    MaterialTheme.colorScheme.onTertiaryContainer,
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.onSurface,
                                ),
                        ) { TextView(stringResource(R.string.adjust)) }
                    }
                }
            },
        )
    } else {
        onDismiss()
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
