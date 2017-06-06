package vince.remotesnoozealarm

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*

class AlarmAdapter constructor(val context: Context, var list: ArrayList<AlarmEntry>): BaseAdapter(), ListAdapter {

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return list.size
    }

    fun add(element: AlarmEntry) {
        list.add(element)
        notifyDataSetChanged()
    }

    fun numberOnly(number: String): String {
        return number.replace("[^0-9]", "")
    }

    fun updateFromSms(number: String) {
        val numbersOnly = numberOnly(number)

        list.map { alarmEntry ->
            if (numbersOnly.contains(alarmEntry.cancelNumber)) {
                alarmEntry.isChecked = false
                alarmEntry.typeface = Typeface.NORMAL
                alarmEntry.cancel()

                notifyDataSetChanged()
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view: View? = convertView
        if (view == null) {
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.alarm_list_entry, parent, false)
        }

        val alarmText: TextView = view!!.findViewById(R.id.alarmText) as TextView
        alarmText.text = list[position].toString()
        alarmText.setTypeface(null, list[position].typeface)

        val alarmSubText: TextView = view.findViewById(R.id.alarmSubText) as TextView
        alarmSubText.text = list[position].subText()
        alarmSubText.setTypeface(null, list[position].typeface)

        val disabledSwitch: Switch = view.findViewById(R.id.alarmState) as Switch
        disabledSwitch.isChecked = list[position].isChecked

        disabledSwitch.setOnClickListener { _->
            if (disabledSwitch.isChecked) {
                // Enable the alarm!
                list[position].enable()
                list[position].typeface = Typeface.BOLD
                list[position].isChecked = true
                alarmText.setTypeface(null, Typeface.BOLD)
                alarmSubText.setTypeface(null, Typeface.BOLD)
            }
            else {
                // Cancel the alarm!
                list[position].cancel()
                list[position].typeface = Typeface.NORMAL
                list[position].isChecked = false
                alarmText.setTypeface(null, Typeface.NORMAL)
                alarmSubText.setTypeface(null, Typeface.NORMAL)
            }

            notifyDataSetChanged()
        }

        alarmText.setOnClickListener { _: View ->
            val year = list[position].alarmDate.get(Calendar.YEAR)
            val month = list[position].alarmDate.get(Calendar.MONTH)
            val dayOfMonth= list[position].alarmDate.get(Calendar.DAY_OF_MONTH)
            val hour = list[position].alarmDate.get(Calendar.HOUR_OF_DAY)
            val minute = list[position].alarmDate.get(Calendar.MINUTE)

            val datePickerListener: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { _: View, year: Int, month: Int, dayOfMonth: Int ->

                val timePickerListener: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener {
                    _: View, hourOfDay: Int, minute: Int ->
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute, 0)

                    val input: EditText = EditText(context)
                    input.inputType = InputType.TYPE_CLASS_PHONE
                    input.gravity = Gravity.CENTER_HORIZONTAL
                    input.textSize = 24.0f
                    input.setText(list[position].cancelNumber)

                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setView(input)

                    builder.setPositiveButton("OK", DialogInterface.OnClickListener { _: DialogInterface, _: Int ->
                        val phoneNumber: String = input.text.toString()
                        list[position].cancelNumber = phoneNumber
                        list[position].alarmDate = calendar
                        list[position].enable()
                        notifyDataSetChanged()
                    })
                    builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                        dialog.cancel()
                    })

                    builder.show()
                }

                val timePicker: TimePickerDialog = TimePickerDialog(context, timePickerListener, hour, minute, false)
                timePicker.show()
            }

            val datePicker: DatePickerDialog = DatePickerDialog(context, datePickerListener, year, month, dayOfMonth)
            datePicker.show()
        }

        return view
    }
}