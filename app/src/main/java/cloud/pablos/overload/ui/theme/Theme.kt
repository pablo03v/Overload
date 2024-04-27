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
        primary = overloadDarkPrimary,
        onPrimary = overloadDarkOnPrimary,
        primaryContainer = overloadDarkPrimaryContainer,
        onPrimaryContainer = overloadDarkOnPrimaryContainer,
        inversePrimary = overloadDarkPrimaryInverse,
        secondary = overloadDarkSecondary,
        onSecondary = overloadDarkOnSecondary,
        secondaryContainer = overloadDarkSecondaryContainer,
        onSecondaryContainer = overloadDarkOnSecondaryContainer,
        tertiary = overloadDarkTertiary,
        onTertiary = overloadDarkOnTertiary,
        tertiaryContainer = overloadDarkTertiaryContainer,
        onTertiaryContainer = overloadDarkOnTertiaryContainer,
        error = overloadDarkError,
        onError = overloadDarkOnError,
        errorContainer = overloadDarkErrorContainer,
        onErrorContainer = overloadDarkOnErrorContainer,
        background = overloadDarkBackground,
        onBackground = overloadDarkOnBackground,
        surface = overloadDarkSurface,
        onSurface = overloadDarkOnSurface,
        inverseSurface = overloadDarkInverseSurface,
        inverseOnSurface = overloadDarkInverseOnSurface,
        surfaceVariant = overloadDarkSurfaceVariant,
        onSurfaceVariant = overloadDarkOnSurfaceVariant,
        outline = overloadDarkOutline,
    )

private val overloadLightColorScheme =
    lightColorScheme(
        primary = overloadLightPrimary,
        onPrimary = overloadLightOnPrimary,
        primaryContainer = overloadLightPrimaryContainer,
        onPrimaryContainer = overloadLightOnPrimaryContainer,
        inversePrimary = overloadLightPrimaryInverse,
        secondary = overloadLightSecondary,
        onSecondary = overloadLightOnSecondary,
        secondaryContainer = overloadLightSecondaryContainer,
        onSecondaryContainer = overloadLightOnSecondaryContainer,
        tertiary = overloadLightTertiary,
        onTertiary = overloadLightOnTertiary,
        tertiaryContainer = overloadLightTertiaryContainer,
        onTertiaryContainer = overloadLightOnTertiaryContainer,
        error = overloadLightError,
        onError = overloadLightOnError,
        errorContainer = overloadLightErrorContainer,
        onErrorContainer = overloadLightOnErrorContainer,
        background = overloadLightBackground,
        onBackground = overloadLightOnBackground,
        surface = overloadLightSurface,
        onSurface = overloadLightOnSurface,
        inverseSurface = overloadLightInverseSurface,
        inverseOnSurface = overloadLightInverseOnSurface,
        surfaceVariant = overloadLightSurfaceVariant,
        onSurfaceVariant = overloadLightOnSurfaceVariant,
        outline = overloadLightOutline,
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
