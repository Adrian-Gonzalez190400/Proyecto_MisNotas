package com.example.misnotas.db
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.misnotas.model.Multimedia

@Dao
interface MultimediaDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMultimedia(multimedia: List<Multimedia>)

    @Query("DELETE FROM Multimedia WHERE noteId = :id")
    fun deleteAllMultimedia(id: Int)

    @Query("SELECT * FROM Multimedia WHERE noteId = :id and type=1 ORDER BY date DESC")
    fun getAllImage(id: Int): MutableList<Multimedia>

    @Query("SELECT * FROM Multimedia WHERE noteId = :id and type=2 ORDER BY date DESC")
    fun getAllVideo(id: Int): MutableList<Multimedia>

    @Query("SELECT * FROM Multimedia WHERE noteId = :id and type=3 ORDER BY date DESC")
    fun getAllVoice(id: Int): MutableList<Multimedia>

    @Query("SELECT * FROM Multimedia WHERE noteId = :id ORDER BY date DESC")
    fun getAllMultimedia(id: Int): MutableList<Multimedia>
}