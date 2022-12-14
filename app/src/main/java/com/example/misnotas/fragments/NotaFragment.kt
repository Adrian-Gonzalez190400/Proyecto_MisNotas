package com.example.misnotas.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.misnotas.R
import com.example.misnotas.activities.MainActivity
import com.example.misnotas.adapters.RvNotesAdapter
import com.example.misnotas.databinding.FragmentNotaBinding
import com.example.misnotas.model.Reminder
import com.example.misnotas.notifications.NotificationReceiver
import com.example.misnotas.utils.SwipeToDelete
import com.example.misnotas.utils.hideKeyboard
import com.example.misnotas.viewModel.MultimediaActivityViewModel
import com.example.misnotas.viewModel.NoteActivityViewModel
import com.example.misnotas.viewModel.ReminderActivityViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class NotaFragment : Fragment(R.layout.fragment_nota) {

    private lateinit var noteBinding: FragmentNotaBinding
    private  val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private  val reminderActivityViewModel: ReminderActivityViewModel by activityViewModels()
    private val multimediaActivityViewModel:MultimediaActivityViewModel by activityViewModels()
    private lateinit var rvAdapter: RvNotesAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        exitTransition=MaterialElevationScale(false).apply {
            duration=350
        }
        enterTransition=MaterialElevationScale(true).apply {
            duration=350
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteBinding=FragmentNotaBinding.bind(view)

        val activity=activity as MainActivity
        val navController=Navigation.findNavController(view)
        requireView().hideKeyboard()
        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
           // activity.window.statusBarColor= Color.WHITE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor=Color.parseColor("#9E9D9D")
        }

        noteBinding.addNoteFab.setOnClickListener{
            noteBinding.appBarLayout.visibility=View.INVISIBLE
            navController.navigate(NotaFragmentDirections.actionNotaFragmentToSaveOrDeleteFragment())
        }

        noteBinding.innerFab.setOnClickListener{
            noteBinding.appBarLayout.visibility=View.INVISIBLE
            navController.navigate(NotaFragmentDirections.actionNotaFragmentToSaveOrDeleteFragment())
        }

        recyclerViewDisplay()
        swipteToDelete(noteBinding.rvNote)

        //implemets search here
        noteBinding.search.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                noteBinding.noData.isVisible=false
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(s.toString().isNotEmpty()){
                    val text=s.toString()
                    val query="%$text%"
                    if(query.isNotEmpty()){
                        noteActivityViewModel.searchNote(query).observe(viewLifecycleOwner){
                            rvAdapter.submitList(it)
                        }
                    }else{
                        observerDataChanges()
                    }
                }else{
                    observerDataChanges()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        noteBinding.search.setOnEditorActionListener{v, actionId, _ ->
            if(actionId==EditorInfo.IME_ACTION_SEARCH){
                v.clearFocus()
                requireView().hideKeyboard()
            }
            return@setOnEditorActionListener true
        }

        noteBinding.rvNote.setOnScrollChangeListener{_,scrollX,scrollY,_,oldScrollY ->
            when{
                scrollY > oldScrollY -> {
                    noteBinding.chatFabText.isVisible=false
                }
                scrollX==scrollY -> {
                    noteBinding.chatFabText.isVisible=true
                }
                else -> {
                    noteBinding.chatFabText.isVisible=true
                }
            }
        }

        val notificationID = activity.intent.getIntExtra("notificationId", -1)
        if(notificationID != -1) openNote(notificationID)
    }

    private fun openNote(notificationID: Int) {
        val noteID = reminderActivityViewModel.getNoteId(notificationID)
        val noteNotification = noteActivityViewModel.getNote(noteID)
        Log.d("IDnotificacion", notificationID.toString())
        Log.d("IDnota", noteID.toString())
        val action=NotaFragmentDirections.actionNotaFragmentToSaveOrDeleteFragment().setNote(noteNotification)
        Navigation.findNavController(noteBinding.rvNote).navigate(action)
        activity?.intent?.putExtra("notificationId", -1)
    }

    private fun swipteToDelete(rvNote: RecyclerView) {
        val swipeToDeleteCallback=object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.absoluteAdapterPosition
                val note=rvAdapter.currentList[position]
                val reminders=reminderActivityViewModel.getAllReminder(note.id)
                val multimedia=multimediaActivityViewModel.getAllMultimedia(note.id)
                var actionBtnTapped=false
                noteActivityViewModel.deleteNote(note)
                cancelAlarms(reminders, note.title)
                noteBinding.search.apply {
                    hideKeyboard()
                    clearFocus()
                }
                if(noteBinding.search.text.toString().isEmpty()){
                    observerDataChanges()
                }
                val snackBar=Snackbar.make(
                    requireView(),getString(R.string.note_deleted), Snackbar.LENGTH_LONG
                ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>(){
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        transientBottomBar?.setAction("UNDO"){
                            scheduleNotifications(reminders, note.title)
                            noteActivityViewModel.saveNote(note, reminders, multimedia)
                            actionBtnTapped=true
                            noteBinding.noData.isVisible=false
                        }
                        super.onShown(transientBottomBar)
                    }
                }).apply {
                    animationMode=Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.add_note_fab)
                }
                snackBar.setActionTextColor(
                    ContextCompat.getColor(
                         requireContext(),
                        R.color.yellowOrange
                    )
                )
                snackBar.show()
            }
        }

        val itemTouchHelper=ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(rvNote)

    }

    private fun observerDataChanges() {
        noteActivityViewModel.getAllNotes().observe(viewLifecycleOwner){list ->
            noteBinding.noData.isVisible=list.isEmpty()
            rvAdapter.submitList(list)
        }
    }

    private fun recyclerViewDisplay() {
        when(resources.configuration.orientation){
            Configuration.ORIENTATION_PORTRAIT -> setUpRecyclerView(2)
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecyclerView(3)
        }
    }

    /*Usaremos adaptador de lista  -> Actualiza en automatico el contenido despues de hacer un cambio */
    private fun setUpRecyclerView(spanCount: Int) {
        noteBinding.rvNote.apply {
            layoutManager=StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            rvAdapter= RvNotesAdapter()
            rvAdapter.stateRestorationPolicy=
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter=rvAdapter
            postponeEnterTransition(300L,TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        observerDataChanges()

    }

    private fun scheduleNotifications(reminders: List<Reminder>, message: String){
        val title = getString(R.string.title_notification)
        reminders.forEach{ reminder ->
            val intent = Intent(activity?.applicationContext, NotificationReceiver::class.java)
            intent.putExtra("titleExtra", title)
            intent.putExtra("messageExtra", message)
            intent.putExtra("notificationId", reminder.notificationId)

            val pendingIntent = PendingIntent.getBroadcast(
                activity?.applicationContext,
                reminder.notificationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.time,
                pendingIntent
            )
        }
    }

    private fun cancelAlarms(reminders: List<Reminder>, message: String){
        val title = getString(R.string.title_notification)
        reminders.forEach{ reminder ->
            val intent = Intent(activity?.applicationContext, NotificationReceiver::class.java)
            intent.putExtra("titleExtra", title)
            intent.putExtra("messageExtra", message)
            intent.putExtra("notificationId", reminder.notificationId)

            val pendingIntent = PendingIntent.getBroadcast(
                activity?.applicationContext,
                reminder.notificationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager!!.cancel(pendingIntent)
        }
    }
}