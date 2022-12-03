package com.example.misnotas.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.misnotas.repository.MultimediaRepository
import com.example.misnotas.repository.NoteRepository
import com.example.misnotas.repository.ReminderRepository

class NoteActivityViewModelFactory(private val noteRepository: NoteRepository,
                                   private val reminderRepository: ReminderRepository,
                                   private val multimediaRepository: MultimediaRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) : T{
        return NoteActivityViewModel(noteRepository, reminderRepository,multimediaRepository) as T
    }
}