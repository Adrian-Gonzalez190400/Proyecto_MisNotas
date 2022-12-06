package com.example.misnotas.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.misnotas.model.Note
import com.example.misnotas.model.Reminder

@Dao
interface ReminderDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminder(reminders: List<Reminder>)

    @Query("DELETE FROM Reminder WHERE noteId = :id")
    fun deleteAllReminder(id: Int)

    @Query("SELECT * FROM Reminder WHERE noteId = :id ORDER BY date DESC")
    fun getAllReminder(id: Int): MutableList<Reminder>

    @Query("SELECT MAX(notificationId) FROM Reminder")
    fun getMaxNotificationId(): Int

    @Query("SELECT noteId FROM Reminder WHERE notificationId = :id")
    fun getNoteId(id: Int): Int
}