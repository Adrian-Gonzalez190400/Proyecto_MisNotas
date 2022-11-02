package com.example.misnotas.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int=0,
    val title: String,
    val content: String,
    val isTask: Boolean,
    val creationDate: String,
    val expirationDate: String?,
    val color: Int = -1
): Serializable