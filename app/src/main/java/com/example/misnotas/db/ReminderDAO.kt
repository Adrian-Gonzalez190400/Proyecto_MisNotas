package com.example.misnotas.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.misnotas.model.Reminder

@Dao
interface ReminderDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Query("SELECT * FROM Reminder ORDER BY date DESC")
    fun getAllReminder(): LiveData<List<Reminder>>

    @Delete
    suspend fun deleteReminder(reminder: Reminder)
}