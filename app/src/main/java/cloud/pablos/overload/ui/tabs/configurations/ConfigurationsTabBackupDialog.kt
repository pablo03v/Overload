package cloud.pablos.overload.ui.tabs.configurations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.SaveAlt
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cloud.pablos.overload.R
import cloud.pablos.overload.ui.views.TextView

@Composable
fun ConfigurationsTabBackupDialog(
    onDismiss: () -> Unit,
    onSaveToDevice: () -> Unit,
    onShare: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors =
                    ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
            ) {
                TextView(stringResource(R.string.cancel))
            }
        },
        modifier = Modifier.padding(16.dp),
        icon = {
            Icon(
                Icons.Rounded.Archive,
                contentDescription = stringResource(R.string.backup),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            TextView(
                stringResource(R.string.backup),
                Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                align = TextAlign.Center,
            )
        },
        text = {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(R.string.backup_descr),
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onSaveToDevice()
                    },
                    Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Rounded.SaveAlt, contentDescription = null)
                        TextView(stringResource(R.string.backup_save_to_device))
                    }
                }

                Button(
                    onClick = {
                        onDismiss()
                        onShare()
                    },
                    Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = null)
                        TextView(stringResource(R.string.backup_share))
                    }
                }
            }
        },
    )
}
