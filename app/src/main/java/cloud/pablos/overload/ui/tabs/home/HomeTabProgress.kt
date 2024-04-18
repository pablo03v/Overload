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
        Modifier.fillMaxWidth(),
        RoundedCornerShape(30.dp),
        MaterialTheme.colorScheme.background,
        tonalElevation = NavigationBarDefaults.Elevation,
    ) {
        Row(
            Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(Modifier.size(30.dp)) {
                val strokeWidth = 6.dp.toPx()

                drawArc(
                    backgroundColor,
                    270f,
                    360f,
                    false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round),
                )

                drawArc(
                    progressData.color,
                    270f,
                    progressData.progress,
                    false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round),
                )
            }
            Column(Modifier.padding(horizontal = 10.dp)) {
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
                MaterialTheme.colorScheme.primary
            }
        }

    // Text
    val title = stringResource(R.string.pause_left)
    val subtitle = getDurationString(Duration.ofMillis(goal - duration))

    HomeTabProgress(
        ProgressData(progress, color),
        title,
        subtitle,
    )
}
