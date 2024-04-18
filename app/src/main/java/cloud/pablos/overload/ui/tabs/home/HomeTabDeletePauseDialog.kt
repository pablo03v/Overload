package cloud.pablos.overload.ui.tabs.home

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import cloud.pablos.overload.R
import cloud.pablos.overload.ui.views.TextView

@Composable
fun HomeTabDeletePauseDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val learnMoreLink = "https://github.com/pabloscloud/Overload?tab=readme-ov-file#why-cant-i-delete-an-ongoing-pause".toUri()

    AlertDialog(
        onDismiss,
        {
            Button(
                { onDismiss() },
                Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            ) {
                TextView(stringResource(R.string.close))
            }
        },
        Modifier.padding(16.dp),
        icon = {
            Icon(
                Icons.Rounded.Info,
                stringResource(R.string.delete_pause),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            TextView(
                stringResource(R.string.delete_pause),
                Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                align = TextAlign.Center,
            )
        },
        text = {
            Column {
                Text(
                    stringResource(R.string.delete_pause_descr),
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

@Preview
@Composable
fun HomeTabDeletePauseDialogPreview() {
    HomeTabDeletePauseDialog {}
}
