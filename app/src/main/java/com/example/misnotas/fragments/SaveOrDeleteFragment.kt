package com.example.misnotas.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.misnotas.R
import com.example.misnotas.activities.MainActivity
import com.example.misnotas.databinding.BottomSheetLayoutBinding
import com.example.misnotas.databinding.FragmentNotaBinding
import com.example.misnotas.databinding.FragmentSaveOrDeleteBinding
import com.example.misnotas.model.Note
import com.example.misnotas.utils.hideKeyboard
import com.example.misnotas.viewModel.NoteActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class SaveOrDeleteFragment : Fragment(R.layout.fragment_save_or_delete) {

    private lateinit var navController: NavController
    private lateinit var contentBinding: FragmentSaveOrDeleteBinding
    private var note: Note?=null
    private var color=-1
    private lateinit var result: String
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private val currentDate = SimpleDateFormat.getInstance().format(Date()) //Obtener fecha actual
    private val job= CoroutineScope(Dispatchers.Main)
    private val args: SaveOrDeleteFragmentArgs by navArgs()
    private val expirationCalendar = Calendar.getInstance()
    private var expirationDate = "Expiration date"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation=MaterialContainerTransform().apply {
            drawingViewId=R.id.fragment
            scrimColor= Color.TRANSPARENT
            duration=300L
        }
        sharedElementEnterTransition=animation
        sharedElementReturnTransition=animation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding=FragmentSaveOrDeleteBinding.bind(view)

        navController=Navigation.findNavController(view)
        val activity=activity as MainActivity

        ViewCompat.setTransitionName(
            contentBinding.noteContentFragmentParent,
            "recyclefiew_${args.note?.id}"
        )

        contentBinding.backBtn. setOnClickListener{
             requireView().hideKeyboard()
            navController.popBackStack()
        }

        contentBinding.saveNote.setOnClickListener{
            saveNote()
        }

        try {
            contentBinding.etNoteContent.setOnFocusChangeListener{_,hasFocus->
                if(hasFocus){
                    contentBinding.bottomBar.visibility=View.VISIBLE
                    contentBinding.etNoteContent.setStylesBar(contentBinding.styleBar)
                }else contentBinding.bottomBar.visibility=View.GONE
            }
        }catch (e: Throwable){
            Log.d("TAG", e.stackTraceToString())
        }

        contentBinding.swTask.setOnClickListener {
            taskItemsVisibility()
        }

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            expirationCalendar.set(Calendar.YEAR, year)
            expirationCalendar.set(Calendar.MONTH, month)
            expirationCalendar.set(Calendar.DAY_OF_MONTH, day)
            expirationDate = SimpleDateFormat.getInstance().format(expirationCalendar.time)
            contentBinding.tvExpirationDate.text = expirationDate
        }

        val timePicker = TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            expirationCalendar.set(Calendar.HOUR_OF_DAY, hour)
            expirationCalendar.set(Calendar.MINUTE, minute)
            expirationDate = SimpleDateFormat.getInstance().format(expirationCalendar.time)
            contentBinding.tvExpirationDate.text = expirationDate
        }

        contentBinding.fabAddExpirationDate.setOnClickListener {
            this.context?.let { it1 ->
                TimePickerDialog(
                    it1, timePicker, expirationCalendar.get(Calendar.HOUR_OF_DAY),
                    expirationCalendar.get(Calendar.MINUTE), false).show()
                DatePickerDialog(
                    it1, datePicker, expirationCalendar.get(Calendar.YEAR),
                    expirationCalendar.get(Calendar.MONTH), expirationCalendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        }

        contentBinding.fabColorPick.setOnClickListener {
            val bottomSheetDialog=BottomSheetDialog(
                requireContext(),
                R.style.BottomSheetDialogTheme
            )
            val bottomSheetView: View=layoutInflater.inflate(
                R.layout.bottom_sheet_layout,
                null,
            )
            with(bottomSheetDialog){
                setContentView(bottomSheetView)
                show()
            }
            val bottomsheetBinding=BottomSheetLayoutBinding.bind(bottomSheetView)
            bottomsheetBinding.apply {
                colorPicker.apply {
                    setSelectedColor(color)
                    setOnColorSelectedListener {
                            value ->
                        color=value
                        contentBinding.apply {
                            noteContentFragmentParent.setBackgroundColor(color)
                            toolbarFragmentNoteContent.setBackgroundColor(color)
                            bottomBar.setBackgroundColor(color)
                            activity.window.statusBarColor=color
                        }
                        bottomsheetBinding.bottomSheetParent.setCardBackgroundColor(color)
                    }
                }
                bottomSheetParent.setCardBackgroundColor(color)
            }
            bottomSheetView.post{
                bottomSheetDialog.behavior.state=BottomSheetBehavior.STATE_EXPANDED
            }
        }
        //opens with existing note item
        setUpNote()
    }

    private fun setUpNote() {
        val note=args.note
        val title=contentBinding.etTitle
        val content=contentBinding.etNoteContent
        val task=contentBinding.swTask
        val expiration=contentBinding.tvExpirationDate
        val lastEdited=contentBinding.lastEdited

        if(note==null){
            contentBinding.lastEdited.text=getString(R.string.edited_on,SimpleDateFormat.getDateInstance().format(Date()))
        }

        if(note!=null){
            title.setText(note.title)
            content.renderMD(note.content)
            task.isChecked=note.isTask
            if(note.isTask) expiration.text=note.expirationDate
            else expiration.text="Expiration date"
            lastEdited.text=getString(R.string.edited_on,note.creationDate)
            color=note.color
            taskItemsVisibility()
            contentBinding.apply {
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(color)
                }
                toolbarFragmentNoteContent.setBackgroundColor(color)
                bottomBar.setBackgroundColor(color)
            }
            activity?.window?.statusBarColor=note.color
        }
    }

    private fun saveNote() {
        if(contentBinding.etNoteContent.text.toString().isEmpty() ||
            contentBinding.etTitle.text.toString().isEmpty() ||
            (contentBinding.swTask.isChecked && expirationDate=="Expiration date")){
            Toast.makeText(activity, "Something is Empty", Toast.LENGTH_SHORT).show()
        }else{
            note=args.note

            when(note){
                null->{
                    noteActivityViewModel.saveNote(
                        Note(
                            0,
                            contentBinding.etTitle.text.toString(),
                            contentBinding.etNoteContent.getMD(),
                            contentBinding.swTask.isChecked,
                            currentDate,
                            expirationDate,
                            color
                        )
                    )
                    result="Note Saved"
                    setFragmentResult(
                        "key",
                        bundleOf("bundleKey" to result)
                    )
                    navController.navigate(SaveOrDeleteFragmentDirections.actionSaveOrDeleteFragmentToNotaFragment())
                }
                else -> {
                    // update note
                    updateNote()
                    navController.popBackStack()
                }
            }


        }
    }

    private fun updateNote() {
        if(note!=null){
             noteActivityViewModel.updateNote(
                 Note(
                     note!!.id,
                     contentBinding.etTitle.text.toString(),
                     contentBinding.etNoteContent.getMD(),
                     contentBinding.swTask.isChecked,
                     currentDate,
                     expirationDate,
                     color
                 )
             )
        }
    }

    private fun taskItemsVisibility(){
        if(contentBinding.swTask.isChecked){
            contentBinding.fabAddExpirationDate.visibility = View.VISIBLE
            contentBinding.fragmentReminder.visibility = View.VISIBLE
        } else{
            contentBinding.fabAddExpirationDate.visibility = View.GONE
            contentBinding.fragmentReminder.visibility = View.GONE
        }
    }

}