/*
 * This portion of code is derived from the Read You app, which is licensed under GNU GPLv3.
 * Original copyright (c) 2022 Ashinch.
 *
 * The portions of code are used under the terms of the GNU GPLv3 license.
 * See https://www.gnu.org/licenses/gpl-3.0.html for more details.
 *
 * Modifications:
 * - colors
 * - size
 * - layout
 * - content
 */

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.Converters.Companion.convertColorToLong
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.ui.views.TextView

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
        "🏋",
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
        "🛋",
        "📺",
    )

@Composable
fun ConfigurationsTabCreateCategoryDialog(
    onClose: () -> Unit,
    categoryEvent: (CategoryEvent) -> Unit,
) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var color by remember { mutableStateOf(colorOptions.first()) }
    var emoji by remember { mutableStateOf(emojiOptions.first()) }

    var nameError by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onClose,
        {
            Button(
                {
                    if (name.text.isEmpty()) {
                        nameError = true
                        return@Button
                    } else {
                        nameError = false
                    }

                    categoryEvent(CategoryEvent.SetName(name.text.replaceFirstChar { it.uppercase() }))
                    categoryEvent(CategoryEvent.SetEmoji(emoji))
                    categoryEvent(CategoryEvent.SetColor(convertColorToLong(color)))
                    categoryEvent(CategoryEvent.SetIsDefault(false))
                    categoryEvent(CategoryEvent.CreateCategory)
                    onClose()
                },
                colors =
                    ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            ) {
                TextView(stringResource(R.string.save))
            }
        },
        Modifier.padding(16.dp),
        {
            Button(
                onClose,
                colors =
                    ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
            ) {
                TextView(stringResource(R.string.cancel))
            }
        },
        title = {
            TextView(
                stringResource(R.string.add_category).replaceFirstChar { it.uppercase() },
                Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                align = TextAlign.Center,
            )
        },
        text = {
            Column(
                Modifier.fillMaxWidth(),
                Arrangement.spacedBy(8.dp),
                Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    Arrangement.spacedBy(8.dp),
                ) {
                    TextView(
                        stringResource(id = R.string.name),
                        fontWeight = FontWeight.Bold,
                        color = if (nameError) MaterialTheme.colorScheme.error else Color.Unspecified,
                    )
                    OutlinedTextField(
                        name,
                        {
                            name = it
                            if (it.text.isNotEmpty()) {
                                nameError = false
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = { Text(stringResource(R.string.name)) },
                        isError = nameError,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        singleLine = true,
                    )
                }

                Column(
                    Modifier.fillMaxWidth(),
                    Arrangement.spacedBy(8.dp),
                ) {
                    TextView(
                        stringResource(R.string.color),
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        Modifier.horizontalScroll(rememberScrollState()),
                        Arrangement.spacedBy(8.dp),
                    ) {
                        colorOptions.forEach { colorOption ->
                            SelectableColor(
                                colorOption == color,
                                { color = colorOption },
                                colorOption,
                            )
                        }
                    }
                }

                Column(
                    Modifier.fillMaxWidth(),
                    Arrangement.spacedBy(8.dp),
                ) {
                    TextView(
                        stringResource(R.string.emoji),
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        Modifier.horizontalScroll(rememberScrollState()),
                        Arrangement.spacedBy(8.dp),
                    ) {
                        emojiOptions.forEach { emojiOption ->
                            SelectableEmoji(
                                emojiOption == emoji,
                                { emoji = emojiOption },
                                emojiOption,
                                color,
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
fun SelectableColor(
    selected: Boolean,
    onClick: () -> Unit,
    color: Color,
    surfaceColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor,
    ) {
        Surface(
            Modifier
                .clickable { onClick() }
                .padding(12.dp)
                .size(34.dp),
            CircleShape,
            color,
        ) {
            Box {
                AnimatedVisibility(
                    selected,
                    Modifier
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    fadeIn() + expandIn(expandFrom = Alignment.Center),
                    shrinkOut(shrinkTowards = Alignment.Center) + fadeOut(),
                ) {
                    Icon(
                        Icons.Outlined.Check,
                        stringResource(R.string.ol_selected),
                        Modifier
                            .padding(5.dp)
                            .size(15.dp),
                        MaterialTheme.colorScheme.surface,
                    )
                }
            }
        }
    }
}

@Composable
fun SelectableEmoji(
    selected: Boolean,
    onClick: () -> Unit,
    emoji: String,
    color: Color,
    surfaceColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
) {
    val surfaceColorSelected = MaterialTheme.colorScheme.primary
    val surfaceColorBySelection = remember { Animatable(if (selected) surfaceColorSelected else surfaceColor) }
    LaunchedEffect(selected) {
        surfaceColorBySelection.animateTo(
            if (selected) surfaceColorSelected else surfaceColor,
            tween(250),
        )
    }

    val bgColor = remember { Animatable(color) }
    LaunchedEffect(color) {
        bgColor.animateTo(
            color,
            tween(250),
        )
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceColorBySelection.value,
    ) {
        Surface(
            Modifier
                .clickable { onClick() }
                .padding(12.dp)
                .size(34.dp),
            CircleShape,
            Color.LightGray,
        ) {
            Row(
                Modifier
                    .clip(CircleShape)
                    .background(bgColor.value)
                    .fillMaxSize(),
                Arrangement.Center,
                Alignment.CenterVertically,
            ) {
                Text(emoji)
            }
        }
    }
}
