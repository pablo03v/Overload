package cloud.pablos.overload.ui.views

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import cloud.pablos.overload.R
import cloud.pablos.overload.data.item.ItemEvent

@Composable
fun ForgotToStopDialog(
    onClose: () -> Unit,
    itemEvent: (ItemEvent) -> Unit,
) {
    val context = LocalContext.current
    val learnMoreLink = "https://codeberg.org/pabloscloud/Overload#spread-acorss-days".toUri()

    AlertDialog(
        onDismissRequest = onClose,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = stringResource(R.string.spans_days),
                tint = MaterialTheme.colorScheme.error,
            )
        },
        title = {
            TextView(
                text = stringResource(R.string.spans_days),
                fontWeight = FontWeight.Bold,
                align = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.spans_days_descr),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(16.dp))

                val openLinkStr = stringResource(id = R.string.open_link_with)
                ClickableText(
                    text = AnnotatedString(stringResource(id = R.string.learn_more)),
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                        ),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, learnMoreLink)
                        val chooserIntent = Intent.createChooser(intent, openLinkStr)
                        ContextCompat.startActivity(context, chooserIntent, null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    itemEvent(ItemEvent.SetSpreadAcrossDaysDialogShown(true))
                    onClose()
                },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
            ) {
                TextView(stringResource(R.string.spread_across_days))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    itemEvent(ItemEvent.SetAdjustEndDialogShown(true))
                    onClose()
                },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextView(stringResource(id = R.string.adjust))
            }
        },
        modifier = Modifier.padding(16.dp),
    )
}
