package com.example.misnotas.repository

import com.example.misnotas.db.NoteDatabase
import com.example.misnotas.model.Reminder

class ReminderRepository(private val db: NoteDatabase) {
    suspend fun addReminder(reminder: Reminder)= db.getReminderDao().addReminder(reminder)
    fun deleteAllReminder(id: Int)= db.getReminderDao().deleteAllReminder(id)
    fun getReminder(id: Int)= db.getReminderDao().getAllReminder(id)
}