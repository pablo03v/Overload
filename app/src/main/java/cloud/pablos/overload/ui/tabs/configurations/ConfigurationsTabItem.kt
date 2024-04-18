package cloud.pablos.overload.ui.tabs.configurations

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cloud.pablos.overload.R

@Composable
fun ConfigurationsTabItem(
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    link: Uri? = null,
    preferenceKey: String? = null,
    switchState: MutableState<Boolean>? = null,
    action: (() -> Unit)? = null,
    background: Boolean = false,
) {
    val context = LocalContext.current

    val openLinkStr = stringResource(R.string.open_link_with)
    Row(
        if (link != null) {
            Modifier
                .clip(shape = RoundedCornerShape(15.dp))
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, link)
                    val chooserIntent = Intent.createChooser(intent, openLinkStr)
                    context.startActivity(chooserIntent)
                }
                .padding(vertical = 10.dp)
                .clearAndSetSemantics {
                    contentDescription = "$title: $description"
                }
        } else if (background && action != null) {
            val shape = RoundedCornerShape(15.dp)
            Modifier
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape)
                .clickable {
                    action()
                }
                .padding(10.dp)
        } else if (action != null) {
            val shape = RoundedCornerShape(15.dp)
            Modifier
                .clip(shape)
                .clickable {
                    action()
                }
                .padding(vertical = 10.dp)
        } else {
            Modifier
                .clearAndSetSemantics {
                    contentDescription = "$title: $description"
                }
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (description != null || background) {
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.spacedBy(15.dp),
                Alignment.CenterVertically,
            ) {
                if (icon != null) {
                    if (background) {
                        Icon(
                            icon,
                            title,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Icon(
                            icon,
                            title,
                            Modifier.padding(horizontal = 15.dp),
                            MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                if (description != null) {
                    Column(Modifier.weight(1f)) {
                        ConfigurationTitle(title)
                        ConfigurationDescription(description)
                    }
                } else {
                    Row(
                        Modifier.fillMaxWidth(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically,
                    ) {
                        ConfigurationTitle(title, MaterialTheme.colorScheme.onSurfaceVariant)
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            title,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                if (preferenceKey != null && switchState != null) {
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

                    AcraSwitch(
                        sharedPreferences,
                        preferenceKey,
                        switchState,
                    ) { newChecked ->
                        sharedPreferences.edit().putBoolean(preferenceKey, newChecked).apply()
                    }
                }
            }
        } else {
            Row {
                ConfigurationLabel(title.replaceFirstChar { it.uppercase() })
            }
        }
    }
}

@Composable
fun AcraSwitch(
    sharedPreferences: SharedPreferences,
    preferenceKey: String,
    state: MutableState<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        state.value,
        { newChecked ->
            state.value = newChecked
            onCheckedChange(newChecked)
            sharedPreferences.edit().putBoolean(preferenceKey, newChecked).apply()
        },
    )
}

@Preview
@Composable
fun ConfigurationsTabItemPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = NavigationBarDefaults.Elevation,
    ) {
        ConfigurationsTabItem(
            "Overload Website",
            "click here to open the website",
            Icons.Rounded.Info,
            "https://overload.pablos.cloud".toUri(),
        )
    }
}
