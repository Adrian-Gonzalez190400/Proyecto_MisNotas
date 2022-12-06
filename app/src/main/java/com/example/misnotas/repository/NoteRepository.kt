package com.example.misnotas.repository

import com.example.misnotas.db.NoteDatabase
import com.example.misnotas.model.Note

class NoteRepository(private val db: NoteDatabase) {
    fun getNote(id: Int)= db.getNoteDao().getNote(id)
    fun getNote()= db.getNoteDao().getAllNote()
    fun getSimpleNote()= db.getNoteDao().getAllSimpleNote()
    fun getTask()= db.getNoteDao().getAllTask()
    fun searchNote(query: String)= db.getNoteDao().searchNote(query)
    suspend fun addNote(note: Note) = db.getNoteDao().addNote(note)
    suspend fun updateNote(note: Note)= db.getNoteDao().updateNote(note)
    suspend fun deleteNote(note: Note)= db.getNoteDao().deleteNote(note)
}