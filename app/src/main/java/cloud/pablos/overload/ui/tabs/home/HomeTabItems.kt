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

val dateFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
val daysBeforeYesterday: Calendar =
    Calendar.getInstance().apply {
        time = Date()
        add(Calendar.DATE, -2)
    }

val dayBeforeYesterday: String = dateFormat.format(daysBeforeYesterday.time)

val dateFormatSymbols: DateFormatSymbols = DateFormatSymbols.getInstance(Locale.ENGLISH)

val dayNames: Array<String> = dateFormatSymbols.weekdays

val dayBeforeYesterdayResId =
    when (dayBeforeYesterday) {
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
val homeTabItems =
    listOf(
        TabItem(
            dayBeforeYesterdayResId,
        ) { categoryState, itemState, itemEvent, listState ->
            DayView(
                categoryState,
                itemState,
                itemEvent,
                LocalDate.now().minusDays(2),
                listState,
            )
        },
        TabItem(
            R.string.yesterday,
        ) { categoryState, itemState, itemEvent, listState ->
            DayView(
                categoryState,
                itemState,
                itemEvent,
                LocalDate.now().minusDays(1),
                listState,
            )
        },
        TabItem(
            R.string.today,
        ) { categoryState, itemState, itemEvent, listState ->
            DayView(
                categoryState,
                itemState,
                itemEvent,
                LocalDate.now(),
                listState,
            )
        },
    )
