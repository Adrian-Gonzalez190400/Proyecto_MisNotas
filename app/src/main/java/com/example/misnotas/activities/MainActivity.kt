package com.example.misnotas.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.misnotas.databinding.ActivityMainBinding
import com.example.misnotas.db.NoteDatabase
import com.example.misnotas.model.Reminder
import com.example.misnotas.repository.NoteRepository
import com.example.misnotas.repository.ReminderRepository
import com.example.misnotas.viewModel.NoteActivityViewModel
import com.example.misnotas.viewModel.NoteActivityViewModelFactory
import com.example.misnotas.viewModel.ReminderActivityViewModel
import com.example.misnotas.viewModel.ReminderActivityViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var noteActivityViewModel: NoteActivityViewModel
    lateinit var reminderActivityViewModel: ReminderActivityViewModel
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding=ActivityMainBinding.inflate(layoutInflater)

        try {
            setContentView(binding.root)
            val noteRepository=NoteRepository(NoteDatabase(this))
            val reminderRepository=ReminderRepository(NoteDatabase(this))
            val noteActivityViewModelFactory=NoteActivityViewModelFactory(noteRepository, reminderRepository)
            val reminderActivityViewModelFactory=ReminderActivityViewModelFactory(reminderRepository)
            noteActivityViewModel=ViewModelProvider(this,
            noteActivityViewModelFactory)[NoteActivityViewModel::class.java]
            reminderActivityViewModel=ViewModelProvider(this,
            reminderActivityViewModelFactory)[ReminderActivityViewModel::class.java]
        }catch (e: Exception){
            Log.d("TAG","Error")
        }

    }
}