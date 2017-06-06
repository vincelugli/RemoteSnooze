package vince.remotesnoozealarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage


class SmsBroadcastReceiver: BroadcastReceiver() {
    val SMS_BUNDLE = "pdus"

    override fun onReceive(context: Context, intent: Intent) {
        val intentExtras: Bundle = intent.extras
        val refreshIntent: Intent = Intent("REFRESH_SMS")

        @Suppress("UNCHECKED_CAST")
        val sms = intentExtras.get(SMS_BUNDLE) as Array<ByteArray>

        for (singleSms in sms) {
            val format: String = intentExtras.getString("format")
            val smsMessage: SmsMessage = SmsMessage.createFromPdu(singleSms, format)

            val smsBody: String = smsMessage.messageBody.toString()
            val address: String = smsMessage.originatingAddress

            // TODO: This is pretty vulnerable to bugs.
            // TODO: You should clean it up (make a list or something...)
            if (smsBody.toLowerCase().contains("i woke up")) {
                refreshIntent.putExtra("number", address)
            }
        }

        context.sendBroadcast(refreshIntent)
    }
}