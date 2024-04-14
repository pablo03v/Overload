package cloud.pablos.overload.ui.tabs.calendar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.Converters.Companion.convertStringToLocalDateTime
import cloud.pablos.overload.data.Helpers.Companion.decideBackground
import cloud.pablos.overload.data.Helpers.Companion.decideForeground
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.views.TextView
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTabTopAppBar(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val yearDialogState = remember { mutableStateOf(false) }
    val categoryDialogState = remember { mutableStateOf(false) }

    val firstYear =
        if (itemState.items.isEmpty()) {
            LocalDate.now().year
        } else {
            itemState.items.minByOrNull { it.startTime }?.let { convertStringToLocalDateTime(it.startTime).year } ?: LocalDate.now().year
        }

    val yearsCount = LocalDate.now().year - firstYear
    val categoriesCount = categoryState.categories.count()
    val selectedCategory = categoryState.categories.find { it.id == categoryState.selectedCategory }

    Surface(
        tonalElevation = NavigationBarDefaults.Elevation,
        color = MaterialTheme.colorScheme.background,
    ) {
        TopAppBar(
            title = {
                TextView(
                    text = stringResource(id = R.string.calendar),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            },
            actions = {
                if (categoriesCount > 1 && selectedCategory != null) {
                    val backgroundColor = decideBackground(categoryState)
                    val foregroundColor = decideForeground(backgroundColor)

                    Button(
                        onClick = { categoryDialogState.value = true },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = backgroundColor,
                                contentColor = foregroundColor,
                            ),
                    ) {
                        TextView(selectedCategory.emoji)
                    }
                    if (categoryDialogState.value) {
                        CalendarTabCategoryDialog(
                            categoryState,
                            categoryEvent,
                            onClose = { categoryDialogState.value = false },
                        )
                    }
                }
                if (yearsCount > 0) {
                    Button(
                        onClick = { yearDialogState.value = true },
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        TextView(itemState.selectedYearCalendar.toString())
                    }
                    if (yearDialogState.value) {
                        CalendarTabYearDialog(
                            firstYear = firstYear,
                            itemEvent = itemEvent,
                            onClose = { yearDialogState.value = false },
                        )
                    }
                }
            },
        )
    }
}
