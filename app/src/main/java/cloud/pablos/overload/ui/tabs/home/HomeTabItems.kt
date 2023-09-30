package cloud.pablos.overload.ui.tabs.home

import android.os.Build
import androidx.annotation.RequiresApi
import cloud.pablos.overload.R
import cloud.pablos.overload.ui.TabItem
import cloud.pablos.overload.ui.views.DayView
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
val daysBeforeYesterday: Calendar = Calendar.getInstance().apply {
    time = Date()
    add(Calendar.DATE, -2)
}

@RequiresApi(Build.VERSION_CODES.O)
val dayBeforeYesterday: String = dateFormat.format(daysBeforeYesterday.time)

@RequiresApi(Build.VERSION_CODES.O)
val dateFormatSymbols: DateFormatSymbols = DateFormatSymbols.getInstance(Locale.getDefault())

@RequiresApi(Build.VERSION_CODES.O)
val dayNames: Array<String> = dateFormatSymbols.weekdays

@RequiresApi(Build.VERSION_CODES.O)
val dayBeforeYesterdayResId = when (dayBeforeYesterday) {
    dayNames[Calendar.MONDAY] -> R.string.monday
    dayNames[Calendar.TUESDAY] -> R.string.tuesday
    dayNames[Calendar.WEDNESDAY] -> R.string.wednesday
    dayNames[Calendar.THURSDAY] -> R.string.thursday
    dayNames[Calendar.FRIDAY] -> R.string.friday
    dayNames[Calendar.SATURDAY] -> R.string.saturday
    dayNames[Calendar.SUNDAY] -> R.string.sunday
    else -> {
        R.string.unknown_day
    }
}

@RequiresApi(Build.VERSION_CODES.S)
val homeTabItems = listOf(
    TabItem(
        titleResId = dayBeforeYesterdayResId,
        screen = { state, onEvent ->
            DayView(
                state = state,
                onEvent = onEvent,
                date = LocalDate.now().minusDays(2),
                isEditable = true,
            )
        },
    ),
    TabItem(
        titleResId = R.string.yesterday,
        screen = { state, onEvent ->
            DayView(
                state = state,
                onEvent = onEvent,
                date = LocalDate.now().minusDays(1),
                isEditable = true,
            )
        },
    ),
    TabItem(
        titleResId = R.string.today,
        screen = { state, onEvent ->
            DayView(
                state = state,
                onEvent = onEvent,
                date = LocalDate.now(),
                isEditable = true,
            )
        },
    ),
)
