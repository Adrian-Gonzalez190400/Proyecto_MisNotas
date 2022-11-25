package com.example.misnotas.repository

import com.example.misnotas.db.NoteDatabase
import com.example.misnotas.model.Reminder

class ReminderRepository(private val db: NoteDatabase) {
    suspend fun addReminder(reminders: List<Reminder>)= db.getReminderDao().addReminder(reminders)
    fun deleteAllReminder(id: Int)= db.getReminderDao().deleteAllReminder(id)
    fun getReminder(id: Int)= db.getReminderDao().getAllReminder(id)
}