package cloud.pablos.overload.ui.screens.day

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import cloud.pablos.overload.R
import cloud.pablos.overload.data.Helpers.Companion.getItems
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.navigation.OverloadRoute
import cloud.pablos.overload.ui.screens.category.CategoryScreenDeleteCategoryDialog

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CategoryScreenBottomAppBar(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
    navController: NavHostController,
) {
    val selectedCategory = categoryState.categories.find { it.id == categoryState.selectedCategoryConfigurations }
    val deleteCategoryDialog = remember { mutableStateOf(false) }

    BottomAppBar(
        actions = {
            IconButton(onClick = { navController.navigate(OverloadRoute.CONFIGURATIONS) }) {
                Icon(
                    Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = "Go Back",
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { deleteCategoryDialog.value = true },
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ) {
                Icon(
                    Icons.Filled.DeleteForever,
                    contentDescription = stringResource(id = R.string.delete_items_forever),
                )
            }
        },
    )

    if (deleteCategoryDialog.value && selectedCategory != null) {
        CategoryScreenDeleteCategoryDialog(
            onDismiss = { deleteCategoryDialog.value = false },
            onConfirm = {
                categoryEvent(CategoryEvent.DeleteCategory(selectedCategory))

                val items = getItems(categoryState, itemState)
                itemEvent(ItemEvent.DeleteItems(items))

                deleteCategoryDialog.value = false
                navController.navigate(OverloadRoute.CONFIGURATIONS)
            },
            category = selectedCategory,
        )
    }
}
