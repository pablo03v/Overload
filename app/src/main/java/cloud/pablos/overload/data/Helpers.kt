package cloud.pablos.overload.data

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cloud.pablos.overload.data.category.CategoryState
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class Helpers {
    companion object {
        private fun calculateRelativeLuminance(color: Color): Double {
            val red = if (color.red <= 0.03928) color.red / 12.92 else ((color.red + 0.055) / 1.055).pow(2.4)
            val green = if (color.green <= 0.03928) color.green / 12.92 else ((color.green + 0.055) / 1.055).pow(2.4)
            val blue = if (color.blue <= 0.03928) color.blue / 12.92 else ((color.blue + 0.055) / 1.055).pow(2.4)
            return 0.2126 * red + 0.7152 * green + 0.0722 * blue
        }

        private fun calculateContrastRatio(
            background: Color,
            foreground: Color,
        ): Double {
            val lum1 = calculateRelativeLuminance(background)
            val lum2 = calculateRelativeLuminance(foreground)
            val lighter = max(lum1, lum2)
            val darker = min(lum1, lum2)
            return (lighter + 0.05) / (darker + 0.05)
        }

        @Composable
        fun decideBackground(categoryState: CategoryState): Color {
            return (
                categoryState.categories.find { category ->
                    category.id == categoryState.selectedCategory
                }
            )
                ?.let {
                    if (it.isDefault) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        Converters.convertLongToColor(it.color)
                    }
                }
                ?: MaterialTheme.colorScheme.primaryContainer
        }

        @Composable
        fun decideForeground(background: Color): Color {
            val white = MaterialTheme.colorScheme.background
            val black = MaterialTheme.colorScheme.onBackground
            val contrastWithWhite = calculateContrastRatio(background, white)
            val contrastWithBlack = calculateContrastRatio(background, black)
            return if (contrastWithWhite >= contrastWithBlack) white else black
        }
    }
}
