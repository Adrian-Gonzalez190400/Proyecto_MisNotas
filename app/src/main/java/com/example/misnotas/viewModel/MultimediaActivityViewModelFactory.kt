package com.example.misnotas.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.misnotas.repository.MultimediaRepository

class MultimediaActivityViewModelFactory(private val repository: MultimediaRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create (modelClass: Class<T>) : T{
        return MultimediaActivityViewModel(repository) as T
    }
}