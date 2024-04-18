package cloud.pablos.overload.ui.screens.category

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cloud.pablos.overload.R
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.ui.views.TextView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreenTopAppBar(categoryState: CategoryState) {
    val selectedCategory = categoryState.categories.find { it.id == categoryState.selectedCategoryConfigurations }

    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = NavigationBarDefaults.Elevation,
    ) {
        TopAppBar(
            {
                TextView(
                    stringResource(R.string.category) + ": " + (selectedCategory?.name ?: "Unknown"),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            },
        )
    }
}
