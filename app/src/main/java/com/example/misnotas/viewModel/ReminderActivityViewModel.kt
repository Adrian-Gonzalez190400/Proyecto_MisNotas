package com.example.misnotas.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misnotas.model.Reminder
import com.example.misnotas.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderActivityViewModel(private val repository: ReminderRepository): ViewModel()  {
    fun saveReminder(newReminder: Reminder)= viewModelScope.launch(Dispatchers.IO) {
        repository.addReminder(newReminder)
    }

    fun updateReminder(existingReminder: Reminder)= viewModelScope.launch(Dispatchers.IO) {
        repository.updateReminder(existingReminder)
    }

    fun deleteReminder(existingReminder: Reminder)= viewModelScope.launch(Dispatchers.IO) {
        repository.deleteReminder(existingReminder)
    }

    fun getAllReminder(): LiveData<List<Reminder>> = repository.getReminder()
}