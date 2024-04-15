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
fun HomeTabDeletePauseDialog(onClose: () -> Unit) {
    val context = LocalContext.current
    val learnMoreLink = "https://github.com/pabloscloud/Overload?tab=readme-ov-file#why-cant-i-delete-an-ongoing-pause".toUri()

    AlertDialog(
        onDismissRequest = onClose,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = stringResource(id = R.string.delete_pause),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            TextView(
                text = stringResource(id = R.string.delete_pause),
                fontWeight = FontWeight.Bold,
                align = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.delete_pause_descr),
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
                onClick = { onClose() },
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            ) {
                TextView(stringResource(id = R.string.close))
            }
        },
        modifier = Modifier.padding(16.dp),
    )
}

@Preview
@Composable
fun HomeTabDeletePauseDialogPreview() {
    HomeTabDeletePauseDialog {}
}
