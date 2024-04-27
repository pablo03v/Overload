package cloud.pablos.overload.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
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

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SwitchCategoryDialog(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismiss,
        content = {
            Surface(
                Modifier.fillMaxWidth(),
                MaterialTheme.shapes.large,
                MaterialTheme.colorScheme.background,
                tonalElevation = NavigationBarDefaults.Elevation,
            ) {
                CategoryDialogContent(categoryState, categoryEvent, onDismiss)
            }
        },
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun CategoryDialogContent(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    onClose: () -> Unit,
) {
    Column(
        Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Rounded.Category,
            stringResource(R.string.select_category),
            Modifier.padding(16.dp),
            MaterialTheme.colorScheme.primary,
        )

        TextView(
            stringResource(R.string.select_category),
            Modifier.padding(top = 16.dp, bottom = 8.dp),
            MaterialTheme.typography.titleLarge.fontSize,
        )

        CategoryListContent(categoryState, categoryEvent, onClose)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
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
        Modifier
            .fillMaxWidth()
            .clickable {
                categoryEvent(CategoryEvent.SetSelectedCategory(category.id))
                onClose()
            }
            .padding(16.dp),
        Arrangement.Center,
    ) {
        TextView(category.emoji + " " + category.name)
    }
}
