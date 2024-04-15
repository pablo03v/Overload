package cloud.pablos.overload.data

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField

class Converters {
    companion object {
        fun convertColorToLong(color: Color): Long {
            val alpha = 255 // (color.alpha * 255).toInt()
            val red = (color.red * 255).toInt()
            val green = (color.green * 255).toInt()
            val blue = (color.blue * 255).toInt()
            return (alpha.toLong() shl 24) or (red.toLong() shl 16) or (green.toLong() shl 8) or blue.toLong()
        }

        fun convertLongToColor(value: Long): Color {
            val alpha = 1f // (value shr 24 and 0xFF).toFloat() / 255f
            val red = (value shr 16 and 0xFF).toFloat() / 255f
            val green = (value shr 8 and 0xFF).toFloat() / 255f
            val blue = (value and 0xFF).toFloat() / 255f
            return Color(red, green, blue, alpha)
        }

        fun convertStringToLocalDateTime(dateTimeString: String): LocalDateTime {
            val formatter =
                DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .optionalStart()
                    .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
                    .optionalEnd()
                    .toFormatter()

            return try {
                LocalDateTime.parse(dateTimeString, formatter)
            } catch (e: DateTimeParseException) {
                return LocalDateTime.now()
            }
        }
    }
}
