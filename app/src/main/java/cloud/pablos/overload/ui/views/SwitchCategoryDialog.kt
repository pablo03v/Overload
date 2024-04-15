package cloud.pablos.overload.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cloud.pablos.overload.R
import cloud.pablos.overload.data.category.Category
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState

@Composable
fun SwitchCategoryDialog(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    onClose: () -> Unit,
) {
    Dialog(
        onDismissRequest = onClose,
        content = {
            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = NavigationBarDefaults.Elevation,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth(),
            ) {
                CategoryDialogContent(categoryState, categoryEvent, onClose)
            }
        },
    )
}

@Composable
private fun CategoryDialogContent(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    onClose: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Category,
            contentDescription = stringResource(id = R.string.select_category),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp),
        )

        TextView(
            text = stringResource(id = R.string.select_category),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )

        CategoryListContent(categoryState, categoryEvent, onClose)
    }
}

@Composable
private fun CategoryListContent(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    onClose: () -> Unit,
) {
    LazyColumn {
        itemsIndexed(categoryState.categories) { index, category ->
            CategoryRow(category, categoryEvent, onClose)
            if (index < categoryState.categories.count() - 1) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    categoryEvent: (CategoryEvent) -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    categoryEvent(CategoryEvent.SetSelectedCategory(category.id))
                    onClose()
                }
                .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        TextView(text = category.emoji + " " + category.name)
    }
}
