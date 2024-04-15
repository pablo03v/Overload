package cloud.pablos.overload.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.data.Helpers.Companion.decideBackground
import cloud.pablos.overload.data.Helpers.Companion.decideForeground
import cloud.pablos.overload.data.Helpers.Companion.getSelectedCategory
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ChangeCategoryButton(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
) {
    val categoryDialogState = remember { mutableStateOf(false) }

    val categoriesCount = categoryState.categories.count()
    val selectedCategory = getSelectedCategory(categoryState)

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
            SwitchCategoryDialog(
                categoryState,
                categoryEvent,
                onClose = { categoryDialogState.value = false },
            )
        }
    }
}
