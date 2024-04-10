package cloud.pablos.overload.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.item.Item
import cloud.pablos.overload.data.item.ItemState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DayViewItemNotOngoing(
    item: Item,
    isSelected: Boolean,
    itemState: ItemState,
) {
    var backgroundColor: Color
    var foregroundColor: Color

    val parsedStartTime: LocalDateTime
    val parsedEndTime: LocalDateTime

    item.let {
        parsedStartTime = parseToLocalDateTime(it.startTime)
        parsedEndTime = parseToLocalDateTime(it.endTime)
        when (isSelected) {
            true -> {
                when (itemState.isDeletingHome) {
                    true -> {
                        backgroundColor = MaterialTheme.colorScheme.errorContainer
                        foregroundColor = MaterialTheme.colorScheme.onErrorContainer
                    }

                    false -> {
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer
                        foregroundColor = MaterialTheme.colorScheme.onPrimaryContainer
                    }
                }
            }

            false -> {
                when (it.pause) {
                    true -> {
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                        foregroundColor = MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    false -> {
                        backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant
                        foregroundColor = MaterialTheme.colorScheme.surfaceVariant
                    }
                }
            }
        }
    }

    val startTimeString = parsedStartTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    val endTimeString = parsedEndTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    val durationString = getDurationString(parsedStartTime, parsedEndTime)

    val itemLabel: String =
        stringResource(
            if (item.pause) R.string.talback_pause else R.string.talback_entry,
            startTimeString,
            endTimeString,
        )

    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(15.dp))
                .background(backgroundColor),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {
                        contentDescription = itemLabel
                    },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextView(
                text = startTimeString,
                color = foregroundColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(12.5.dp),
            )

            if (item.pause) {
                Icon(
                    Icons.Outlined.DarkMode,
                    contentDescription = stringResource(id = R.string.pause),
                    tint = foregroundColor,
                )
            }

            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(10.dp)
                        .offset(x = 5.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(foregroundColor),
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(id = R.string.arrow_forward),
                        tint = foregroundColor,
                        modifier =
                            Modifier
                                .offset(x = (-5).dp)
                                .size(25.dp),
                    )
                }
            }

            TextView(
                text = endTimeString,
                color = foregroundColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(12.5.dp),
            )
        }

        TextView(
            text = durationString,
            color = foregroundColor,
            fontWeight = FontWeight.Medium,
            align = TextAlign.Center,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .background(backgroundColor)
                    .padding(8.dp),
        )
    }
}
