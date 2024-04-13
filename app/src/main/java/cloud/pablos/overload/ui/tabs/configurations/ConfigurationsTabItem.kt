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
    link: Uri? = null,
    icon: ImageVector? = null,
    preferenceKey: String? = null,
    switchState: MutableState<Boolean>? = null,
    action: (() -> Unit)? = null,
    background: Boolean = false,
) {
    val context = LocalContext.current

    val openLinkStr = stringResource(id = R.string.open_link_with)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
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
    ) {
        if (description != null || background) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint =
                            if (!background) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        modifier =
                            if (!background) {
                                Modifier.padding(horizontal = 15.dp)
                            } else {
                                Modifier
                            },
                    )
                }
                if (description != null) {
                    Column(modifier = Modifier.weight(1f)) {
                        ConfigurationTitle(title)
                        ConfigurationDescription(description)
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        ConfigurationTitle(title, MaterialTheme.colorScheme.onSurfaceVariant)
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = title,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                if (preferenceKey != null && switchState != null) {
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

                    AcraSwitch(
                        sharedPreferences = sharedPreferences,
                        preferenceKey = preferenceKey,
                        state = switchState,
                        onCheckedChange = { newChecked ->
                            sharedPreferences.edit().putBoolean(preferenceKey, newChecked).apply()
                        },
                    )
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
        checked = state.value,
        onCheckedChange = { newChecked ->
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
        tonalElevation = NavigationBarDefaults.Elevation,
        color = MaterialTheme.colorScheme.background,
    ) {
        ConfigurationsTabItem(
            title = "F-Droid",
            description = "please support them",
            link = "https://f-droid.org".toUri(),
            icon = Icons.Rounded.Info,
        )
    }
}
