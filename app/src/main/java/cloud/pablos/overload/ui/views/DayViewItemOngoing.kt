package cloud.pablos.overload.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import cloud.pablos.overload.data.Helpers
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.Item
import cloud.pablos.overload.data.item.ItemState
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DayViewItemOngoing(
    item: Item,
    categoryState: CategoryState,
    itemState: ItemState,
    isSelected: Boolean = false,
    showDate: Boolean = false,
    hideEnd: Boolean = false,
) {
    val backgroundColorCategory = Helpers.decideBackground(categoryState)
    val foregroundColorCategory = Helpers.decideForeground(backgroundColorCategory)

    var backgroundColor: Color
    var foregroundColor: Color
    var blink by remember { mutableStateOf(true) }

    LaunchedEffect(blink) {
        while (true) {
            delay(500) // Blink every 500ms
            blink = blink.not()
        }
    }

    val parsedStartTime: LocalDateTime
    val parsedEndTime: LocalDateTime

    item.let {
        parsedStartTime = parseToLocalDateTime(it.startTime)
        parsedEndTime = LocalDateTime.now()

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
                        backgroundColor = backgroundColorCategory
                        foregroundColor = foregroundColorCategory
                    }
                }
            }
        }
    }

    val currentTime = LocalTime.now()
    val currentHour = currentTime.format(DateTimeFormatter.ofPattern("HH"))
    val currentMinute = currentTime.format(DateTimeFormatter.ofPattern("mm"))

    val startTimeString: String =
        parsedStartTime.format(DateTimeFormatter.ofPattern(if (showDate) "MM/dd/yy HH:mm" else "HH:mm"))

    val durationString = getDurationString(parsedStartTime, parsedEndTime)

    val itemLabel: String =
        stringResource(
            if (item.pause) R.string.talback_pause_ongoing else R.string.talback_entry_ongoing,
            startTimeString,
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

            if (hideEnd.not()) {
                Row(modifier = Modifier.padding(12.5.dp)) {
                    TextView(
                        text = currentHour,
                        color = foregroundColor,
                        fontWeight = FontWeight.Medium,
                    )
                    AnimatedVisibility(
                        visible = blink,
                        enter = fadeIn(),
                        exit = fadeOut(animationSpec = tween(1000)),
                    ) {
                        TextView(
                            text = ":",
                            color = foregroundColor,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    TextView(
                        text = currentMinute,
                        color = foregroundColor,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        if (hideEnd.not()) {
            AnimatedVisibility(
                visible = blink,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(1000)),
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .background(backgroundColor),
            ) {
                TextView(
                    text = durationString,
                    color = foregroundColor,
                    fontWeight = FontWeight.Medium,
                    align = TextAlign.Center,
                    modifier =
                        Modifier
                            .padding(8.dp),
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun getDurationString(
    parsedStartTime: LocalDateTime,
    parsedEndTime: LocalDateTime,
): String {
    val duration: Duration = Duration.between(parsedStartTime, parsedEndTime)
    val hours = duration.toHours()
    val minutes: Long =
        if (isToMinutesPartAvailable()) {
            duration.toMinutesPart().toLong()
        } else {
            duration.toMinutes() % 60
        }
    return when {
        hours > 0 -> "$hours h $minutes min"
        minutes > 0 -> "$minutes min"
        else -> "0 min"
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun getDurationString(duration: Duration): String {
    val hours = duration.toHours()
    val minutes: Long =
        if (isToMinutesPartAvailable()) {
            duration.toMinutesPart().toLong()
        } else {
            duration.toMinutes() % 60
        }
    return when {
        hours > 0 -> "$hours h $minutes min"
        minutes > 0 -> "$minutes min"
        else -> "0 min"
    }
}

fun isToMinutesPartAvailable(): Boolean {
    return try {
        Duration::class.java.getMethod("toMinutesPart")
        true
    } catch (e: NoSuchMethodException) {
        false
    }
}
