package com.example.misnotas.viewModel

import androidx.lifecycle.ViewModel
import com.example.misnotas.model.Multimedia
import com.example.misnotas.repository.MultimediaRepository

class MultimediaActivityViewModel(private val repository: MultimediaRepository): ViewModel() {
    fun getAllMultimedia(id: Int): MutableList<Multimedia> = repository.getMultimedia(id)
}