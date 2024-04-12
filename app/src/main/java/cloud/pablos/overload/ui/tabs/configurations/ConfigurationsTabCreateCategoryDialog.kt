package cloud.pablos.overload.ui.tabs.configurations

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.Converters.Companion.convertColorToLong
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.ui.views.TextView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationsTabCreateCategoryDialog(
    onClose: () -> Unit,
    categoryEvent: (CategoryEvent) -> Unit,
) {
    val colorOptions: List<Color> =
        listOf(
            Color(255, 209, 220), // Pastel Pink
            Color(255, 204, 204), // Pastel Red
            Color(250, 223, 173), // Pastel Yellow
            Color(255, 230, 204), // Light Orange
            Color(204, 255, 229), // Pastel Green
            Color(230, 255, 204), // Light Lime
            Color(221, 204, 255), // Pastel Purple
            Color(230, 204, 255), // Light Indigo
            Color(204, 230, 255), // Pastel Blue
            Color(204, 255, 255), // Light Cyan
            Color(255, 204, 230), // Light Magenta
            Color(255, 230, 255), // Light Lavender
        )

    val emojiOptions: List<String> =
        listOf(
            "💼",
            "👔",
            "💻",
            "🖋️",
            "📚",
            "🎓",
            "📝",
            "✏️",
            "🏋️‍♂️",
            "🚴",
            "🏃",
            "⛹️‍♀️",
            "🎉",
            "🍻",
            "🎮",
            "🍹",
            "👨‍👩‍👧‍👦",
            "👪",
            "🏡",
            "🎨",
            "🎸",
            "🎮",
            "📷",
            "🍳",
            "🍔",
            "🍕",
            "🥗",
            "✈️",
            "🚗",
            "🚢",
            "🌍",
            "💊",
            "🧘",
            "🏥",
            "🌱",
            "🛀",
            "🌅",
            "🛋️",
            "📺",
        )

    var name by remember { mutableStateOf(TextFieldValue()) }
    var color by remember { mutableStateOf(colorOptions.first()) }
    var emoji by remember { mutableStateOf(emojiOptions.first()) }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            TextView(
                text = "Create Category",
                fontWeight = FontWeight.Bold,
                align = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = "Name") },
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextView(
                        "Color",
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        modifier =
                            Modifier
                                .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        colorOptions.forEach { colorOption ->
                            SelectableColor(
                                selected = colorOption == color,
                                onClick = { color = colorOption },
                                color = colorOption,
                            )
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextView(
                        "Emoji",
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        modifier =
                            Modifier
                                .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        emojiOptions.forEach { emojiOption ->
                            SelectableEmoji(
                                selected = emojiOption == emoji,
                                onClick = { emoji = emojiOption },
                                emoji = emojiOption,
                                color = color,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    categoryEvent(CategoryEvent.SetName(name.text.replaceFirstChar { it.uppercase() }))
                    categoryEvent(CategoryEvent.SetEmoji(emoji))
                    categoryEvent(CategoryEvent.SetColor(convertColorToLong(color)))
                    categoryEvent(CategoryEvent.SetIsDefault(false))
                    categoryEvent(CategoryEvent.SaveCategory)
                    onClose()
                },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            ) {
                TextView(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            Button(
                onClick = onClose,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        modifier = Modifier.padding(16.dp),
    )
}

private fun (() -> Unit).save(
    sharedPreferences: OlSharedPreferences,
    hours: Int?,
    minutes: Int?,
    valid: Boolean,
    isPause: Boolean,
) {
    if (valid) {
        val hoursInMin = (hours ?: 0) * 60
        val minutesInMin = minutes ?: 0
        val goal = (hoursInMin + minutesInMin) * 60 * 1000

        if (goal > 0) {
            when (isPause) {
                true -> sharedPreferences.savePauseGoal(goal)
                false -> sharedPreferences.saveWorkGoal(goal)
            }
            this()
        }
    }
}

@Composable
fun SelectableColor(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.inverseOnSurface,
    ) {
        Surface(
            modifier =
                Modifier
                    .clickable { onClick() }
                    .padding(12.dp)
                    .size(34.dp),
            shape = CircleShape,
            color = color,
        ) {
            Box {
                AnimatedVisibility(
                    visible = selected,
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                    enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                    exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut(),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Checked",
                        modifier =
                            Modifier
                                .padding(5.dp)
                                .size(15.dp),
                        tint = MaterialTheme.colorScheme.surface,
                    )
                }
            }
        }
    }
}

@Composable
fun SelectableEmoji(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    emoji: String,
    color: Color,
) {
    val surfaceColorUnselected = MaterialTheme.colorScheme.inverseOnSurface
    val surfaceColorSelected = MaterialTheme.colorScheme.primary
    val surfaceColor = remember { Animatable(if (selected) surfaceColorSelected else surfaceColorUnselected) }
    LaunchedEffect(selected) {
        surfaceColor.animateTo(
            if (selected) surfaceColorSelected else surfaceColorUnselected,
            animationSpec = tween(250),
        )
    }

    val bgColor = remember { Animatable(color) }
    LaunchedEffect(color) {
        bgColor.animateTo(
            color,
            animationSpec = tween(250),
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor.value,
    ) {
        Surface(
            modifier =
                Modifier
                    .clickable { onClick() }
                    .padding(12.dp)
                    .size(34.dp),
            shape = CircleShape,
            color = Color.LightGray,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(bgColor.value)
                        .fillMaxSize(),
            ) {
                Text(emoji)
            }
        }
    }
}
