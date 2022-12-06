package com.example.misnotas.viewModel

import androidx.lifecycle.ViewModel
import com.example.misnotas.model.Multimedia
import com.example.misnotas.repository.MultimediaRepository

class MultimediaActivityViewModel(private val repository: MultimediaRepository): ViewModel() {
    fun getAllMultimedia(id: Int): MutableList<Multimedia> = repository.getMultimedia(id)
    fun getAllImage(id: Int) : MutableList<Multimedia> = repository.getAllImage(id)
    fun getAllVideo(id: Int) : MutableList<Multimedia> = repository.getAllVideo(id)
    fun getAllVoice(id: Int) : MutableList<Multimedia> = repository.getAllVoice(id)
}