package vince.remotesnoozealarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmIntent: Intent = Intent("ALARM_RECEIVER_UNIQ")

        context.sendBroadcast(alarmIntent)
    }
}