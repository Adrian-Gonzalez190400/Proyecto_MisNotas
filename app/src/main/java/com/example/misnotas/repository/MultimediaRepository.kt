package com.example.misnotas.repository

import com.example.misnotas.db.NoteDatabase
import com.example.misnotas.model.Multimedia

class MultimediaRepository(private val db: NoteDatabase){
    suspend fun addMultimedia(multimedia: List<Multimedia>)= db.getMultimediaDao().addMultimedia(multimedia)
    fun deleteAllMultimedia(id: Int)= db.getMultimediaDao().deleteAllMultimedia(id)
    fun getAllImage(id: Int)=db.getMultimediaDao().getAllImage(id)
    fun getAllVideo(id: Int)=db.getMultimediaDao().getAllVideo(id)
    fun getAllVoice(id: Int)=db.getMultimediaDao().getAllVoice(id)
    fun getMultimedia(id: Int)= db.getMultimediaDao().getAllMultimedia(id)
}