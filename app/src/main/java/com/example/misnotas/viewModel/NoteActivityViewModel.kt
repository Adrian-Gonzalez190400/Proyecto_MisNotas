package com.example.misnotas.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misnotas.fragments.DataSourceReminder
import com.example.misnotas.model.Multimedia
import com.example.misnotas.model.Note
import com.example.misnotas.model.Reminder
import com.example.misnotas.repository.MultimediaRepository
import com.example.misnotas.repository.NoteRepository
import com.example.misnotas.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteActivityViewModel(private val noteRepository: NoteRepository,
                            private val reminderRepository: ReminderRepository,
                            private val multimediaRepository: MultimediaRepository): ViewModel() {
    fun saveNote(newNote: Note)= viewModelScope.launch(Dispatchers.IO) {
        noteRepository.addNote(newNote)
    }

    fun saveNote(newNote: Note, lstReminder: List<Reminder>, lstMultimedia: List<Multimedia>) = viewModelScope.launch(Dispatchers.IO) {
        val noteId = noteRepository.addNote(newNote)
        lstReminder.forEach {reminder ->  reminder.noteId = noteId.toInt()}
        reminderRepository.addReminder(lstReminder)

        lstMultimedia.forEach{multimedia -> multimedia.noteId=noteId.toInt() }
        multimediaRepository.addMultimedia(lstMultimedia)

    }

    fun updateNote(existingNote: Note)= viewModelScope.launch(Dispatchers.IO) {
        noteRepository.updateNote(existingNote)
    }

    fun updateNote(existingNote: Note, lstReminder: List<Reminder>, lstMultimedia: List<Multimedia>)= viewModelScope.launch(Dispatchers.IO) {
        noteRepository.updateNote(existingNote)
        reminderRepository.deleteAllReminder(existingNote.id)
        multimediaRepository.deleteAllMultimedia(existingNote.id)

        lstReminder.forEach {reminder ->  reminder.noteId = existingNote.id}
        reminderRepository.addReminder(lstReminder)

        lstMultimedia.forEach{multimedia -> multimedia.noteId=existingNote.id}
        multimediaRepository.addMultimedia(lstMultimedia)
    }

    fun deleteNote(existingNote: Note)= viewModelScope.launch(Dispatchers.IO) {
        noteRepository.deleteNote(existingNote)
        reminderRepository.deleteAllReminder(existingNote.id)
        multimediaRepository.deleteAllMultimedia(existingNote.id)
    }

    fun searchNote(query: String): LiveData<List<Note>>{
        return noteRepository.searchNote(query)
    }

    fun getAllNotes(): LiveData<List<Note>> = noteRepository.getNote()

    fun getAllSimpleNotes(): LiveData<List<Note>> = noteRepository.getSimpleNote()

    fun getAllTasks(): LiveData<List<Note>> = noteRepository.getTask()
}