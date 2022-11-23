package com.example.misnotas.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.misnotas.model.Note

@Dao
interface NoteDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT * FROM Note ORDER BY creationDate DESC")
    fun getAllNote(): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE isTask=false ORDER BY creationDate DESC")
    fun getAllSimpleNote(): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE isTask=true  ORDER BY creationDate DESC")
    fun getAllTask(): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE title LIKE :query OR content LIKE :query ORDER BY creationDate DESC")
    fun searchNote(query: String): LiveData<List<Note>>

    @Query("SELECT MAX(id) FROM Note")
    fun getLastNoteId(): Int

    @Delete
    suspend fun deleteNote(note: Note)
}