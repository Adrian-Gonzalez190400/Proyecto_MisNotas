package com.example.misnotas.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Multimedia (
    @PrimaryKey(autoGenerate = true)
    var id: Int=0,
    var noteId: Int,
    val type: Int,
    val path: String,
    var date: String
): Serializable
