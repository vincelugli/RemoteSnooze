package vince.remotesnoozealarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.graphics.Typeface
import java.util.*

class AlarmEntry constructor(var cancelNumber: String, var alarmDate: Calendar, val pendingIntent: PendingIntent, val alarmMgr: AlarmManager, var typeface: Int, var isChecked: Boolean) {
    fun cancel() {
        alarmMgr.cancel(pendingIntent)
    }

    fun enable() {
        cancel()
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmDate.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    fun formatStr(value: Int): String {
        val retStr: String
        if (value < 10) {
            retStr = "0$value"
        }
        else {
            retStr = value.toString()
        }

        return retStr
    }

    override fun toString(): String {
        val year: Int = alarmDate.get(Calendar.YEAR)

        val month: Int = alarmDate.get(Calendar.MONTH)
        val monthStr: String = formatStr(month)

        val dayOfMonth: Int = alarmDate.get(Calendar.DAY_OF_MONTH)
        val dayStr: String = formatStr(dayOfMonth)

        val minute: Int = alarmDate.get(Calendar.MINUTE)
        val minuteStr: String = formatStr(minute)

        val hour: Int = alarmDate.get(Calendar.HOUR)
        val hourStr: String = formatStr(hour)

        return "$hourStr:$minuteStr $monthStr/$dayStr/$year"
    }

    fun subText(): String {
        return cancelNumber
    }
}
