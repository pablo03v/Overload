package cloud.pablos.overload.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Material 3 color schemes
private val overloadDarkColorScheme =
    darkColorScheme(
        overloadDarkPrimary,
        overloadDarkOnPrimary,
        overloadDarkPrimaryContainer,
        overloadDarkOnPrimaryContainer,
        overloadDarkPrimaryInverse,
        overloadDarkSecondary,
        overloadDarkOnSecondary,
        overloadDarkSecondaryContainer,
        overloadDarkOnSecondaryContainer,
        overloadDarkTertiary,
        overloadDarkOnTertiary,
        overloadDarkTertiaryContainer,
        overloadDarkOnTertiaryContainer,
        overloadDarkError,
        overloadDarkOnError,
        overloadDarkErrorContainer,
        overloadDarkOnErrorContainer,
        overloadDarkBackground,
        overloadDarkOnBackground,
        overloadDarkSurface,
        overloadDarkOnSurface,
        overloadDarkInverseSurface,
        overloadDarkInverseOnSurface,
        overloadDarkSurfaceVariant,
        overloadDarkOnSurfaceVariant,
        overloadDarkOutline,
    )

private val overloadLightColorScheme =
    lightColorScheme(
        overloadLightPrimary,
        overloadLightOnPrimary,
        overloadLightPrimaryContainer,
        overloadLightOnPrimaryContainer,
        overloadLightPrimaryInverse,
        overloadLightSecondary,
        overloadLightOnSecondary,
        overloadLightSecondaryContainer,
        overloadLightOnSecondaryContainer,
        overloadLightTertiary,
        overloadLightOnTertiary,
        overloadLightTertiaryContainer,
        overloadLightOnTertiaryContainer,
        overloadLightError,
        overloadLightOnError,
        overloadLightErrorContainer,
        overloadLightOnErrorContainer,
        overloadLightBackground,
        overloadLightOnBackground,
        overloadLightSurface,
        overloadLightOnSurface,
        overloadLightInverseSurface,
        overloadLightInverseOnSurface,
        overloadLightSurfaceVariant,
        overloadLightOnSurfaceVariant,
        overloadLightOutline,
    )

@Composable
fun OverloadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val overloadColorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> overloadDarkColorScheme
            else -> overloadLightColorScheme
        }
    val view = LocalView.current
    if (view.isInEditMode.not()) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = overloadColorScheme.surfaceColorAtElevation(3.dp).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        overloadColorScheme,
        shapes,
        overloadTypography,
        content,
    )
}
