package cloud.pablos.overload.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cloud.pablos.overload.R

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AddCategoryButton(onClose: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth(),
        Arrangement.Center,
    ) {
        FilledTonalButton({ onClose() }) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Outlined.Add,
                    stringResource(R.string.add_category),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                TextView(
                    stringResource(R.string.add_category),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}
