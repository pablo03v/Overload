package cloud.pablos.overload.ui.views

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cloud.pablos.overload.R
import cloud.pablos.overload.data.Converters.Companion.convertLongToColor
import cloud.pablos.overload.data.Converters.Companion.convertStringToLocalDateTime
import cloud.pablos.overload.data.category.Category
import cloud.pablos.overload.data.item.Item
import cloud.pablos.overload.ui.tabs.home.HomeTabProgress
import cloud.pablos.overload.ui.tabs.home.ProgressData
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@SuppressLint("UnusedTransitionTargetStateParameter")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DayViewProgress(
    category: Category,
    items: List<Item>,
    goal: Int,
    isPause: Boolean,
    date: LocalDate = LocalDate.now(),
) {
    val duration: Long
    var count: Long = 0

    // Filter items by type
    val itemsFiltered =
        items.filter { item ->
            when (isPause) {
                true -> {
                    item.pause
                }

                false -> {
                    item.pause.not()
                }
            }
        }

    // Count duration
    itemsFiltered.forEach {
        val parsedStartTime = convertStringToLocalDateTime(it.startTime)
        val parsedEndTime =
            if (it.ongoing) {
                LocalDateTime.now()
            } else {
                convertStringToLocalDateTime(it.endTime)
            }

        count += Duration.between(parsedStartTime, parsedEndTime).toMillis()
    }

    // If last item is not a pause, automatically count duration between then and now
    if (
        isPause &&
        items.last().pause.not() &&
        date == LocalDate.now()
    ) {
        val parsedStartTime = convertStringToLocalDateTime(items.last().endTime)
        val parsedEndTime = LocalDateTime.now()

        count += Duration.between(parsedStartTime, parsedEndTime).toMillis()
    }
    duration = count

    val label = "progress"

    // Animation
    val transition = updateTransition(duration, label)

    // Progress
    val progress =
        transition.animateFloat(
            { tween(800) },
            label,
        ) { remTime ->
            val calculatedProgress =
                if (remTime < 0) {
                    360f
                } else {
                    360f - ((360f / goal) * (goal - remTime))
                }

            calculatedProgress.coerceAtMost(360f)
        }

    // Color
    val color =
        transition.animateColor(
            {
                tween(800, easing = LinearEasing)
            },
            label,
        ) {
            if (progress.value < 360f) {
                MaterialTheme.colorScheme.error
            } else {
                convertLongToColor(category.color)
            }
        }

    // Text
    val title =
        when (isPause) {
            true -> {
                stringResource(R.string.pause_left)
            }

            false -> {
                "Time left"
            }
        }
    val subtitle = getDurationString(Duration.ofMillis(goal - duration))

    HomeTabProgress(
        ProgressData(progress, color),
        title,
        subtitle,
    )
}
