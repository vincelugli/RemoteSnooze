package vince.remotesnoozealarm

import android.Manifest
import android.app.*
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.*
import android.graphics.Typeface
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.Gravity
import com.google.gson.Gson
import java.util.*
import kotlin.collections.HashSet


class MainActivity: AppCompatActivity() {

//    private val smsMessageList: ArrayList<String> = ArrayList<String>()
//    private val smsManager: SmsManager = SmsManager.getDefault()

//    private lateinit var messages: ListView
//    private lateinit var messageArrayAdapter: ArrayAdapter<String>
//    private lateinit var input: EditText

    private var alarmEntries: ArrayList<AlarmEntry> = ArrayList<AlarmEntry>()
    private var nextInt = 0

    private lateinit var alarms: ListView
    private lateinit var alarmArrayAdapter: AlarmAdapter
    private lateinit var alarmMgr: AlarmManager

    private var broadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.extras.getString("number") != null) {
//                updateInbox(intent.extras.getString("message"))
                alarmArrayAdapter.updateFromSms(intent.extras.getString("number"))
            }
        }
    }

    private var alarmTriggerReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone: Ringtone = RingtoneManager.getRingtone(context, notification)

            ringtone.play()

            val input: TextView = TextView(context)
            input.setText("Time to get up!")
            input.gravity = Gravity.CENTER_HORIZONTAL or(Gravity.CENTER_VERTICAL)
            input.textSize = 24.0f
            input.setPadding(8, 8, 8, 8)

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setView(input)

            builder.setPositiveButton("OK", DialogInterface.OnClickListener { _: DialogInterface, _: Int ->
                ringtone.stop()
            })

            builder.show()
        }
    }

    private var isReceiverRegistered: Boolean = false
    private var isAlarmReceiverRegistered: Boolean = false

    private val RECEIVE_SMS_PERMISSIONS_REQUEST = 1
    private val COMPLETED_ONBOARDING_PREF_NAME: String = "HAS_COMPLETED_ONBOARDING"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        // Check if we need to onboard!
        if (!sharedPreferences.getBoolean(COMPLETED_ONBOARDING_PREF_NAME, false)) {
            startActivity(Intent(this, OnboardingActivity::class.java))
        }
        else {
            // Alarm things
            alarms = findViewById(R.id.alarmListView) as ListView
            alarmArrayAdapter = AlarmAdapter(this, alarmEntries)
            alarms.adapter = alarmArrayAdapter

            alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            registerReceiver(broadcastReceiver, IntentFilter("REFRESH_SMS"))
            registerReceiver(alarmTriggerReceiver, IntentFilter("ALARM_RECEIVER_UNIQ"))
            isReceiverRegistered = true
            isAlarmReceiverRegistered = true
        }

//        val host: TabHost = findViewById(R.id.tabHost) as TabHost
//        host.setup()
//
//        var spec = host.newTabSpec("Tab One")
//        spec.setContent(R.id.tab1)
//        spec.setIndicator("Tab One")
//        host.addTab(spec)
//
//        spec = host.newTabSpec("Tab Two")
//        spec.setContent(R.id.tab2)
//        spec.setIndicator("Tab Two")
//        host.addTab(spec)

//        // Message things
//        messages = findViewById(R.id.messageListView) as ListView
//        input = findViewById(R.id.messageInput) as EditText
//        messageArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessageList)
//        messages.adapter = messageArrayAdapter

//        else {
//            initSmsInbox()
//        }

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
//            getPermissionToReceiveSMS()
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//            getPermissionToReadSMS()
//        }
    }

    override fun onDetachedFromWindow() {
        unregisterReceiver(broadcastReceiver)
        unregisterReceiver(alarmTriggerReceiver)
        isReceiverRegistered = false
        isAlarmReceiverRegistered = false
        super.onDetachedFromWindow()
    }

//    fun onSendButtonClick(view: View) {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//            getPermissionToReadSMS()
//        }
//        else {
//            smsManager.sendTextMessage("+15555215554", null, input.text.toString(), null, null)
//            input.text.clear()
//            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show()
//        }
//    }

    fun getPermissionToReceiveSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                Toast.makeText(this, "This will allow us to stop your alarm!", Toast.LENGTH_LONG).show()
            }
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.RECEIVE_SMS), RECEIVE_SMS_PERMISSIONS_REQUEST)
        }
    }

//    fun getPermissionToReadSMS() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
//                Toast.makeText(this, "This will allow us to stop your alarm!", Toast.LENGTH_LONG).show()
//            }
//            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.READ_SMS), READ_SMS_PERMISSIONS_REQUEST)
//        }
//    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<String>,
                                   grantResults: IntArray) {
        when (requestCode) {
//            READ_SMS_PERMISSIONS_REQUEST -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show()
////                    initSmsInbox()
//                } else {
//                    Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
            RECEIVE_SMS_PERMISSIONS_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Receive SMS permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Receive SMS permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

//    fun initSmsInbox() {
//        val smsInboxCursor: Cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)
//        try {
//            smsInboxCursor.use {
//                val indexBody: Int = smsInboxCursor.getColumnIndex("body")
//                val indexAddress: Int = smsInboxCursor.getColumnIndex("address")
//
//                if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return
//
//                messageArrayAdapter.clear()
//
//                do {
//                    val nextMessage: String = "SMS from: " + smsInboxCursor.getString(indexAddress) + "\n" + smsInboxCursor.getString(indexBody) + "\n"
//                    messageArrayAdapter.add(nextMessage)
//                } while (smsInboxCursor.moveToNext())
//            }
//        }
//        finally {
//            smsInboxCursor.close()
//        }
//    }

//    fun updateInbox(message: String) {
//        messageArrayAdapter.insert(message, 0)
//    }

    fun onAddAlarmButtonClick(view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReceiveSMS()
        }
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val dayOfMonth= c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val datePickerListener: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { _: View, year: Int, month: Int, dayOfMonth: Int ->

            val timePickerListener: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener {
                _: View, hourOfDay: Int, minute: Int ->
                val calendar: Calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth, hourOfDay, minute, 0)

                val input: EditText = EditText(this)
                input.inputType = InputType.TYPE_CLASS_PHONE
                input.gravity = Gravity.CENTER_HORIZONTAL
                input.textSize = 24.0f

                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setView(input)

                builder.setPositiveButton("OK", DialogInterface.OnClickListener { _: DialogInterface, _: Int ->
                    val phoneNumber: String = input.text.toString()

                    val alarmIntent: Intent = Intent(this, AlarmReceiver::class.java)
                    val alarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, ++nextInt, alarmIntent, 0)

                    alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                            AlarmManager.INTERVAL_DAY, alarmPendingIntent)

                    val alarmEntry: AlarmEntry = AlarmEntry(phoneNumber, calendar, alarmPendingIntent, alarmMgr, Typeface.BOLD, true)
                    alarmArrayAdapter.add(alarmEntry)
                })
                builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                    dialog.cancel()
                })

                builder.show()
            }

            val timePicker: TimePickerDialog = TimePickerDialog(this, timePickerListener, hour, minute, false)
            timePicker.show()
        }

        val datePicker: DatePickerDialog = DatePickerDialog(this, datePickerListener, year, month, dayOfMonth)
        datePicker.show()
    }
}

