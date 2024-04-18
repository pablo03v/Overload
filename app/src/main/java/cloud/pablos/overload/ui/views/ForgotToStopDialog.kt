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
import cloud.pablos.overload.data.Helpers.Companion.decideBackground
import cloud.pablos.overload.data.Helpers.Companion.decideForeground
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.ItemEvent

@Composable
fun ForgotToStopDialog(
    onDismiss: () -> Unit,
    categoryState: CategoryState,
    itemEvent: (ItemEvent) -> Unit,
) {
    val backgroundColor = decideBackground(categoryState)
    val foregroundColor = decideForeground(backgroundColor)

    val context = LocalContext.current
    val learnMoreLink = "https://github.com/pabloscloud/Overload?tab=readme-ov-file#why-does-the-app-annoy-me-with-a-popup-to-adjust-the-end".toUri()

    AlertDialog(
        onDismiss,
        {
            Button(
                {
                    itemEvent(ItemEvent.SetSpreadAcrossDaysDialogShown(true))
                    onDismiss()
                },
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
            ) {
                TextView(stringResource(R.string.spread_across_days))
            }
        },
        modifier = Modifier.padding(16.dp),
        {
            Button(
                {
                    itemEvent(ItemEvent.SetAdjustEndDialogShown(true))
                    onDismiss()
                },
                Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        backgroundColor,
                        foregroundColor,
                    ),
            ) {
                TextView(stringResource(R.string.adjust))
            }
        },
        icon = {
            Icon(
                Icons.Rounded.Info,
                stringResource(R.string.spans_days),
                tint = MaterialTheme.colorScheme.error,
            )
        },
        title = {
            TextView(
                stringResource(R.string.spans_days),
                Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                align = TextAlign.Center,
                maxLines = 2,
            )
        },
        text = {
            Column {
                Text(
                    stringResource(R.string.spans_days_descr),
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(16.dp))

                val openLinkStr = stringResource(R.string.open_link_with)
                ClickableText(
                    AnnotatedString(stringResource(R.string.learn_more)),
                    Modifier.fillMaxWidth(),
                    MaterialTheme.typography.bodyMedium.copy(
                        MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                    ),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, learnMoreLink)
                        val chooserIntent = Intent.createChooser(intent, openLinkStr)
                        ContextCompat.startActivity(context, chooserIntent, null)
                    },
                )
            }
        },
    )
}
