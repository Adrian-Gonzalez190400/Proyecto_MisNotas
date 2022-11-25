package com.example.misnotas.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misnotas.model.Reminder
import com.example.misnotas.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderActivityViewModel(private val repository: ReminderRepository): ViewModel()  {
    fun getAllReminder(id: Int): MutableList<Reminder> = repository.getReminder(id)
}