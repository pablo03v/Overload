package cloud.pablos.overload.ui.screens.category

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dangerous
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.data.category.Category
import cloud.pablos.overload.ui.views.TextView

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CategoryScreenDeleteCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    category: Category,
) {
    AlertDialog(
        onDismiss,
        {
            Button(
                { onConfirm() },
                colors =
                    ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.errorContainer,
                        MaterialTheme.colorScheme.onErrorContainer,
                    ),
            ) {
                TextView(stringResource(R.string.yes))
            }
        },
        Modifier.padding(16.dp),
        {
            Button(
                { onDismiss() },
                colors =
                    ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
            ) {
                TextView(stringResource(R.string.no))
            }
        },
        {
            Icon(
                Icons.Rounded.Dangerous,
                stringResource(R.string.delete_category),
                tint = MaterialTheme.colorScheme.error,
            )
        },
        {
            TextView(
                stringResource(R.string.delete_category),
                Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                align = TextAlign.Center,
                maxLines = 2,
            )
        },
        {
            Column {
                Text(
                    stringResource(
                        R.string.delete_category_warning,
                        category.name,
                    ),
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
    )
}
