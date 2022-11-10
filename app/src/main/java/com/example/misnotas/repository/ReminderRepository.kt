package com.example.misnotas.repository

import com.example.misnotas.db.NoteDatabase
import com.example.misnotas.model.Reminder

class ReminderRepository(private val db: NoteDatabase) {
    fun getReminder()= db.getReminderDao().getAllReminder()
    suspend fun addReminder(reminder: Reminder)= db.getReminderDao().addReminder(reminder)
    suspend fun updateReminder(reminder: Reminder)= db.getReminderDao().updateReminder(reminder)
    suspend fun deleteReminder(reminder: Reminder)= db.getReminderDao().deleteReminder(reminder)
}