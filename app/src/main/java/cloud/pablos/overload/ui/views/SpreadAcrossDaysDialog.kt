package cloud.pablos.overload.ui.views

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.Item
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SpreadAcrossDaysDialog(
    onDismiss: () -> Unit,
    categoryState: CategoryState,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val backgroundColor = decideBackground(categoryState)
    val foregroundColor = decideForeground(backgroundColor)

    val context = LocalContext.current
    val learnMoreLink = "https://github.com/pabloscloud/Overload?tab=readme-ov-file#why-does-the-app-annoy-me-with-a-popup-to-adjust-the-end".toUri()

    val date = LocalDate.now()

    val itemsNotToday =
        itemState.items.filter { item ->
            val startTime = convertStringToLocalDateTime(item.startTime)
            extractDate(startTime) != date
        }
    val isOngoingNotToday = itemsNotToday.isNotEmpty() && itemsNotToday.any { it.ongoing }
    val firstOngoingItem = itemsNotToday.find { it.ongoing }

    if (isOngoingNotToday && firstOngoingItem != null) {
        AlertDialog(
            onDismiss,
            {
                Button(
                    onClick = {
                        onDismiss()
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = backgroundColor,
                            contentColor = foregroundColor,
                        ),
                ) {
                    TextView(stringResource(R.string.no))
                }
            },
            Modifier.padding(16.dp),
            {
                Button(
                    { onDismiss.save(itemEvent, firstOngoingItem) },
                    colors =
                        ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                ) {
                    TextView(stringResource(R.string.yes))
                }
            },
            {
                Icon(
                    Icons.Rounded.Info,
                    stringResource(R.string.spread_across_days),
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            {
                TextView(
                    stringResource(R.string.spread_across_days),
                    Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    align = TextAlign.Center,
                    maxLines = 2,
                )
            },
            {
                Column {
                    Text(
                        stringResource(R.string.are_you_sure),
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
) {
    val currentDate = LocalDate.now()

    val startTime = convertStringToLocalDateTime(item.startTime)
    val startDate = startTime.toLocalDate()
    var dateIterator = startTime.toLocalDate()

    while (dateIterator.isBefore(currentDate) || dateIterator == currentDate) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val newStartTime = if (dateIterator == startDate) startTime else LocalDateTime.of(dateIterator, LocalTime.MIDNIGHT)
        val newEndTime =
            if (dateIterator == currentDate) {
                convertStringToLocalDateTime(
                    LocalDateTime.now().toString(),
                )
            } else {
                LocalDateTime.of(dateIterator, LocalTime.MAX)
            }

        if (dateIterator == startDate) {
            itemEvent(ItemEvent.SetId(item.id))
        }
        itemEvent(ItemEvent.SetStart(newStartTime.format(formatter)))
        itemEvent(ItemEvent.SetEnd(newEndTime.format(formatter)))
        itemEvent(ItemEvent.SetOngoing(false))
        itemEvent(ItemEvent.SetPause(item.pause))
        itemEvent(ItemEvent.SaveItem)
        dateIterator = dateIterator.plusDays(1)
    }

    this()
}
