package com.example.misnotas.adapters

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.misnotas.R
import com.example.misnotas.activities.MainActivity
import com.example.misnotas.databinding.ReminderItemLayoutBinding
import com.example.misnotas.fragments.ReminderFragment
import com.example.misnotas.model.Reminder
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.*

class RvReminderAdapter: ListAdapter<Reminder, RvReminderAdapter.ReminderViewHolder>(DiffUtilCallbackReminder()) {

    inner class ReminderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val contenBinding=ReminderItemLayoutBinding.bind(itemView)
        val date: MaterialTextView=contenBinding.reminderTime
        val edit: FloatingActionButton=contenBinding.fabEditReminder
        val delete: FloatingActionButton=contenBinding.fabDeleteReminder
        val parent: MaterialCardView=contenBinding.reminderItemLayoutParent
        val myCalendar= Calendar.getInstance()
        lateinit var newDate: String
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.reminder_item_layout,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        getItem(position).let { reminder ->
            holder.apply {
                parent.transitionName="recyclerView_${reminder.id}"
                date.text=reminder.date

//                val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, day ->
//                    myCalendar.set(Calendar.YEAR, year)
//                    myCalendar.set(Calendar.MONTH, month)
//                    myCalendar.set(Calendar.DAY_OF_MONTH, day)
//                    newDate = SimpleDateFormat.getInstance().format(myCalendar.time)
//                }
//
//                val timePicker = TimePickerDialog.OnTimeSetListener { view, hour, minute ->
//                    myCalendar.set(Calendar.HOUR_OF_DAY, hour)
//                    myCalendar.set(Calendar.MINUTE, minute)
//                    newDate = SimpleDateFormat.getInstance().format(myCalendar.time)
//                }
//
//                edit.setOnClickListener {
//                    TimePickerDialog(
//                        it.context, timePicker, myCalendar.get(Calendar.HOUR_OF_DAY),
//                        myCalendar.get(Calendar.MINUTE), false).show()
//                    DatePickerDialog(
//                        it.context, datePicker, myCalendar.get(Calendar.YEAR),
//                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
//                    val actpapa = MainActivity()
//                    actpapa.updateReminder(
//                        Reminder(
//                            reminder.id,
//                            newDate
//                        )
//                    )
//                }
//
//                delete.setOnClickListener {
//
//                }
            }

        }
    }

}