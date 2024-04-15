package cloud.pablos.overload.ui.tabs.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.ui.views.TextView
import cloud.pablos.overload.ui.views.getDurationString
import java.time.Duration

@Composable
fun HomeTabProgress(
    progressData: ProgressData,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    Surface(
        tonalElevation = NavigationBarDefaults.Elevation,
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(30.dp),
        modifier =
            Modifier
                .fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(15.dp),
        ) {
            Canvas(
                modifier = modifier.size(30.dp),
            ) {
                val strokeWidth = 6.dp.toPx()

                drawArc(
                    startAngle = 270f, // 270 is 0 degree
                    sweepAngle = 360f,
                    useCenter = false,
                    color = backgroundColor,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )

                drawArc(
                    startAngle = 270f, // 270 is 0 degree
                    sweepAngle = progressData.progress,
                    useCenter = false,
                    color = progressData.color,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }
            Column(
                modifier = Modifier.padding(horizontal = 10.dp),
            ) {
                TextView(title, maxLines = 2)
                TextView(subtitle)
            }
        }
    }
}

class ProgressData(
    progress: State<Float>,
    color: State<Color>,
) {
    val progress by progress
    val color by color
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun HomeTabProgressPreview() {
    val goal: Long = 3600000
    val duration: Long = 2700000

    // Animation
    val transition = updateTransition(targetState = duration, label = "progress")

    // Progress
    val progress =
        transition.animateFloat(
            transitionSpec = { tween(800) },
            label = "progress",
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
            transitionSpec = {
                tween(800, easing = LinearEasing)
            },
            label = "Color transition",
        ) {
            if (progress.value < 360f) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }
        }

    // Text
    val title = stringResource(id = R.string.pause_left)
    val subtitle = getDurationString(Duration.ofMillis(goal - duration))

    HomeTabProgress(
        progressData = ProgressData(progress = progress, color = color),
        title = title,
        subtitle = subtitle,
    )
}
