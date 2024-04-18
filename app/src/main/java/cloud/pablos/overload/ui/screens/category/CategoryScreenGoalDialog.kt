package cloud.pablos.overload.ui.screens.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.category.Category
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.ui.views.TextView

@Composable
fun CategoryScreenGoalDialog(
    category: Category,
    categoryEvent: (CategoryEvent) -> Unit,
    onClose: () -> Unit,
    isPause: Boolean,
) {
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }

    val hoursValidator = (hours.toIntOrNull() ?: 0) < 24
    val minValidator = (minutes.toIntOrNull() ?: 0) < 60

    val hoursFocusRequest = remember { FocusRequester() }
    val minFocusRequest = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        hoursFocusRequest.requestFocus()
    }

    AlertDialog(
        onClose,
        {
            Button(
                {
                    onClose.save(
                        category,
                        categoryEvent,
                        hours.toIntOrNull(),
                        minutes.toIntOrNull(),
                        hoursValidator && minValidator,
                        isPause,
                    )
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
                Text(stringResource(R.string.cancel))
            }
        },
        title = {
            TextView(
                if (isPause) {
                    stringResource(R.string.set_goal_pause)
                } else {
                    stringResource(R.string.set_goal)
                },
                Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                align = TextAlign.Center,
            )
        },
        text = {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TimeInput(
                    stringResource(R.string.hours),
                    hours,
                    { hours = it },
                    hoursValidator.not(),
                    hoursFocusRequest,
                )

                TimeInput(
                    stringResource(R.string.minutes),
                    minutes,
                    { minutes = it },
                    minValidator.not(),
                    minFocusRequest,
                )
            }
        },
    )
}

@Composable
fun TimeInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    focusRequester: FocusRequester,
) {
    TextField(
        value,
        onValueChange,
        Modifier.focusRequester(focusRequester),
        placeholder = { Text("0") },
        trailingIcon = { Text(label, Modifier.padding(end = 10.dp)) },
        isError = isError,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions { focusRequester.requestFocus() },
        singleLine = true,
    )
}

private fun (() -> Unit).save(
    category: Category,
    categoryEvent: (CategoryEvent) -> Unit,
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
            saveGoal(category, categoryEvent, goal, isPause)
            this()
        }
    }
}

fun saveGoal(
    category: Category,
    categoryEvent: (CategoryEvent) -> Unit,
    goal: Int,
    isPause: Boolean,
) {
    categoryEvent(CategoryEvent.SetId(category.id))
    categoryEvent(CategoryEvent.SetName(category.name))
    categoryEvent(CategoryEvent.SetColor(category.color))
    categoryEvent(CategoryEvent.SetEmoji(category.emoji))
    categoryEvent(CategoryEvent.SetIsDefault(category.isDefault))

    if (isPause) {
        categoryEvent(CategoryEvent.SetGoal1(category.goal1))
        categoryEvent(CategoryEvent.SetGoal2(goal))
    } else {
        categoryEvent(CategoryEvent.SetGoal1(goal))
        categoryEvent(CategoryEvent.SetGoal2(category.goal2))
    }

    categoryEvent(CategoryEvent.SaveCategory)
}
