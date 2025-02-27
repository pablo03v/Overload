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
import cloud.pablos.overload.data.Converters.Companion.convertStringToLocalDateTime
import cloud.pablos.overload.data.Helpers.Companion.decideBackground
import cloud.pablos.overload.data.Helpers.Companion.decideForeground
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
    val backgroundColorCategory = decideBackground(categoryState)
    val foregroundColorCategory = decideForeground(backgroundColorCategory)

    var backgroundColor: Color
    var foregroundColor: Color
    var blink by remember { mutableStateOf(true) }

    LaunchedEffect(blink) {
        while (true) {
            delay(500)
            blink = blink.not()
        }
    }

    val parsedStartTime: LocalDateTime
    val parsedEndTime: LocalDateTime

    item.let {
        parsedStartTime = convertStringToLocalDateTime(it.startTime)
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
        Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(backgroundColor),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clearAndSetSemantics {
                    contentDescription = itemLabel
                },
            Arrangement.SpaceBetween,
            Alignment.CenterVertically,
        ) {
            TextView(
                startTimeString,
                Modifier.padding(12.5.dp),
                fontWeight = FontWeight.Medium,
                color = foregroundColor,
            )

            if (item.pause) {
                Icon(
                    Icons.Outlined.DarkMode,
                    stringResource(R.string.pause),
                    tint = foregroundColor,
                )
            }

            Box(
                Modifier
                    .weight(1f)
                    .padding(10.dp)
                    .offset(x = 5.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(foregroundColor),
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        stringResource(R.string.arrow_forward),
                        Modifier
                            .offset(x = (-5).dp)
                            .size(25.dp),
                        foregroundColor,
                    )
                }
            }

            if (hideEnd.not()) {
                Row(Modifier.padding(12.5.dp)) {
                    TextView(
                        currentHour,
                        fontWeight = FontWeight.Medium,
                        color = foregroundColor,
                    )
                    AnimatedVisibility(
                        blink,
                        enter = fadeIn(),
                        exit = fadeOut(tween(1000)),
                    ) {
                        TextView(
                            ":",
                            fontWeight = FontWeight.Medium,
                            color = foregroundColor,
                        )
                    }
                    TextView(
                        currentMinute,
                        fontWeight = FontWeight.Medium,
                        color = foregroundColor,
                    )
                }
            }
        }

        if (hideEnd.not()) {
            AnimatedVisibility(
                blink,
                Modifier
                    .align(Alignment.Center)
                    .background(backgroundColor),
                fadeIn(),
                fadeOut(tween(1000)),
            ) {
                TextView(
                    durationString,
                    Modifier.padding(8.dp),
                    fontWeight = FontWeight.Medium,
                    color = foregroundColor,
                    align = TextAlign.Center,
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
