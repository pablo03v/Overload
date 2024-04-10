package cloud.pablos.overload.ui.screens.day

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.navigation.OverloadRoute
import cloud.pablos.overload.ui.navigation.OverloadTopAppBar
import cloud.pablos.overload.ui.views.TextView

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CategoryScreen(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val selectedCategory = categoryState.categories.find { it.id == categoryState.selectedCategoryConfigurations }

    Scaffold(
        topBar = {
            OverloadTopAppBar(
                selectedDestination = OverloadRoute.CATEGORY,
                categoryState = categoryState,
                itemState = itemState,
                itemEvent = itemEvent,
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TextView(text = "lmao")
        }
    }
}
