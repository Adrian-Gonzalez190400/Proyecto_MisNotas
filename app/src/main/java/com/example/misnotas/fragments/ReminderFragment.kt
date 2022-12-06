package com.example.misnotas.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.misnotas.R
import com.example.misnotas.activities.MainActivity
import com.example.misnotas.adapters.RvReminderAdapter
import com.example.misnotas.databinding.FragmentReminderBinding
import com.example.misnotas.utils.hideKeyboard
import com.example.misnotas.model.Reminder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ReminderFragment : Fragment(R.layout.fragment_reminder)  {
    private lateinit var reminderBinding: FragmentReminderBinding
    private lateinit var rvAdapter: RvReminderAdapter
    private val myCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        exitTransition= MaterialElevationScale(false).apply {
            duration=350
        }
        enterTransition= MaterialElevationScale(true).apply {
            duration=350
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reminderBinding= FragmentReminderBinding.bind(view)
        val activity=activity as MainActivity
        requireView().hideKeyboard()

        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            // activity.window.statusBarColor= Color.WHITE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor= Color.parseColor("#9E9D9D")
        }

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)
        }

        val timePicker = TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hour)
            myCalendar.set(Calendar.MINUTE, minute)
            addReminder(
                Reminder(
                    0, 0, 0, SimpleDateFormat.getInstance().format(myCalendar.time), myCalendar.timeInMillis
                )
            )
        }

        reminderBinding.fabAddReminder.setOnClickListener {
            this.context?.let { it1 ->
                TimePickerDialog(
                    it1, timePicker, myCalendar.get(Calendar.HOUR_OF_DAY),
                    myCalendar.get(Calendar.MINUTE), false).show()
                DatePickerDialog(
                    it1, datePicker, myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        }

        recyclerViewDisplay()

        dataChanged()

        reminderBinding.rvReminder.setOnScrollChangeListener{_,scrollX,scrollY,_,oldScrollY ->
            when{
                scrollY > oldScrollY -> {
                    reminderBinding.fabTextReminder.isVisible=false
                }
                scrollX==scrollY -> {
                    reminderBinding.fabTextReminder.isVisible=true
                }
                else -> {
                    reminderBinding.fabTextReminder.isVisible=true
                }
            }
        }
    }

    private fun dataChanged(){
        rvAdapter.submitList(DataSourceReminder.lstReminder)
        reminderBinding.rvReminder.adapter?.notifyDataSetChanged()
    }

    private fun addReminder(rem: Reminder){
        DataSourceReminder.lstReminder.add(rem)
        dataChanged()
    }

    private fun recyclerViewDisplay() {
        when(resources.configuration.orientation){
            Configuration.ORIENTATION_PORTRAIT -> setUpRecyclerView(2)
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecyclerView(3)
        }
    }

    /*Usaremos adaptador de lista  -> Actualiza en automatico el contenido despues de hacer un cambio */
    private fun setUpRecyclerView(spanCount: Int) {
        reminderBinding.rvReminder.apply {
            layoutManager= LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            rvAdapter= RvReminderAdapter(/*DataSourceReminder.lstReminder*/)
            rvAdapter.stateRestorationPolicy=
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter=rvAdapter
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }
}