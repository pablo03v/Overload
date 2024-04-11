package cloud.pablos.overload.ui.tabs.configurations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun ConfigurationsTabCreateCategoryDialog(
    onClose: () -> Unit,
    categoryEvent: (CategoryEvent) -> Unit,
) {
    var name by remember { mutableStateOf(TextFieldValue()) }

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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    categoryEvent(CategoryEvent.SetName(name.text))
                    categoryEvent(CategoryEvent.SetEmoji("💙"))
                    categoryEvent(CategoryEvent.SetColor(convertColorToLong(Color.Blue)))
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
