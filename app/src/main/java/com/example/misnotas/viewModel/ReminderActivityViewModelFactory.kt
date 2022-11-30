package com.example.misnotas.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.misnotas.repository.ReminderRepository

class ReminderActivityViewModelFactory(private val repository: ReminderRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) : T{
        return ReminderActivityViewModel(repository) as T
    }
}