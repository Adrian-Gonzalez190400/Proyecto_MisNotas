package com.example.misnotas.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    var id: Int=0,
    //var idNote: Int,
    val date: String
): Serializable