package cloud.pablos.overload.ui.screens.category

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.Converters.Companion.convertColorToLong
import cloud.pablos.overload.data.Converters.Companion.convertLongToColor
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.navigation.OverloadRoute
import cloud.pablos.overload.ui.navigation.OverloadTopAppBar
import cloud.pablos.overload.ui.tabs.configurations.ConfigurationDescription
import cloud.pablos.overload.ui.tabs.configurations.ConfigurationTitle
import cloud.pablos.overload.ui.tabs.configurations.ConfigurationsTabItem
import cloud.pablos.overload.ui.tabs.configurations.HoDivider
import cloud.pablos.overload.ui.tabs.configurations.SelectableColor
import cloud.pablos.overload.ui.tabs.configurations.SelectableEmoji
import cloud.pablos.overload.ui.tabs.configurations.colorOptions
import cloud.pablos.overload.ui.tabs.configurations.emojiOptions

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CategoryScreen(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val selectedCategory = categoryState.categories.find { it.id == categoryState.selectedCategoryConfigurations }
    var name by remember { mutableStateOf(TextFieldValue(selectedCategory?.name ?: "")) }
    var color by remember { mutableStateOf(selectedCategory?.color?.let { convertLongToColor(it) } ?: Color.Unspecified) }
    var emoji by remember { mutableStateOf(selectedCategory?.emoji ?: "") }

    var nameError by remember { mutableStateOf(false) }

    LaunchedEffect(color, emoji) {
        if (selectedCategory != null) {
            save(
                categoryEvent,
                selectedCategory.id,
                name.text,
                selectedCategory.goal1,
                selectedCategory.goal2,
                convertColorToLong(color),
                emoji,
                selectedCategory.isDefault,
            )
        }
    }

    val goalDialogState = remember { mutableStateOf(false) }
    val pauseGoalDialogState = remember { mutableStateOf(false) }

    if (selectedCategory != null) {
        Scaffold(
            topBar = {
                OverloadTopAppBar(
                    selectedDestination = OverloadRoute.CATEGORY,
                    categoryState = categoryState,
                    categoryEvent = categoryEvent,
                    itemState = itemState,
                    itemEvent = itemEvent,
                )
            },
        ) { paddingValues ->
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                item {
                    ConfigurationsTabItem(title = "Name")
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it

                                if (it.text.isNotEmpty()) {
                                    nameError = false
                                }
                            },
                            singleLine = true,
                            placeholder = { Text(text = "Name") },
                            isError = nameError,
                            keyboardActions =
                                KeyboardActions(
                                    onDone = {
                                        if (name.text.isEmpty()) {
                                            nameError = true
                                            return@KeyboardActions
                                        } else {
                                            nameError = false
                                        }

                                        save(
                                            categoryEvent,
                                            selectedCategory.id,
                                            name.text,
                                            selectedCategory.goal1,
                                            selectedCategory.goal2,
                                            convertColorToLong(color),
                                            emoji,
                                            selectedCategory.isDefault,
                                        )
                                    },
                                ),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                if (selectedCategory.isDefault.not()) {
                    item {
                        HoDivider()
                    }

                    item {
                        ConfigurationsTabItem(title = "Color")
                    }

                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
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
                                        surfaceColor = MaterialTheme.colorScheme.surfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    HoDivider()
                }

                item {
                    ConfigurationsTabItem(title = "Emoji")
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
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
                                    surfaceColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            }
                        }
                    }
                }

                item {
                    HoDivider()
                }

                item {
                    ConfigurationsTabItem(title = stringResource(id = R.string.goals))
                }

                // Goal 1
                item {
                    val itemLabel = stringResource(id = R.string.work) + ": " + stringResource(id = R.string.work_goal_descr)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            Modifier
                                .padding(bottom = 16.dp)
                                .clip(shape = RoundedCornerShape(15.dp))
                                .clickable {
                                    goalDialogState.value = true
                                }
                                .clearAndSetSemantics {
                                    contentDescription = itemLabel
                                },
                    ) {
                        Text(
                            selectedCategory.emoji,
                            modifier =
                                Modifier
                                    .width(40.dp)
                                    .padding(horizontal = 8.dp),
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column {
                                ConfigurationTitle(selectedCategory.name)
                                ConfigurationDescription("Set a goal for " + selectedCategory.name)
                            }
                        }
                    }
                }

                // Goal 2
                item {
                    val itemLabel = stringResource(id = R.string.pause) + ": " + stringResource(id = R.string.pause_goal_descr)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            Modifier
                                .padding(bottom = 16.dp)
                                .clip(shape = RoundedCornerShape(15.dp))
                                .clickable {
                                    pauseGoalDialogState.value = true
                                }
                                .clearAndSetSemantics {
                                    contentDescription = itemLabel
                                },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DarkMode,
                            contentDescription = stringResource(id = R.string.pause),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier =
                                Modifier
                                    .width(40.dp)
                                    .padding(horizontal = 8.dp),
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column {
                                ConfigurationTitle(stringResource(id = R.string.pause))
                                ConfigurationDescription(stringResource(id = R.string.pause_goal_descr))
                            }
                        }
                    }
                }
            }
        }

        if (goalDialogState.value) {
            CategoryScreenGoalDialog(
                onClose = { goalDialogState.value = false },
                isPause = false,
                category = selectedCategory,
                categoryEvent = categoryEvent,
            )
        }

        if (pauseGoalDialogState.value) {
            CategoryScreenGoalDialog(
                onClose = { pauseGoalDialogState.value = false },
                isPause = true,
                category = selectedCategory,
                categoryEvent = categoryEvent,
            )
        }
    }
}

fun save(
    categoryEvent: (CategoryEvent) -> Unit,
    id: Int,
    name: String,
    goal1: Int,
    goal2: Int,
    color: Long,
    emoji: String,
    isDefault: Boolean,
) {
    categoryEvent(CategoryEvent.SetId(id))
    categoryEvent(CategoryEvent.SetName(name))
    categoryEvent(CategoryEvent.SetColor(color))
    categoryEvent(CategoryEvent.SetGoal1(goal1))
    categoryEvent(CategoryEvent.SetGoal2(goal2))
    categoryEvent(CategoryEvent.SetEmoji(emoji))
    categoryEvent(CategoryEvent.SetIsDefault(isDefault))
    categoryEvent(CategoryEvent.SaveCategory)

    Log.d("category save", "yeeeeah weird")
}
