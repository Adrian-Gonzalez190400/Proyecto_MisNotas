package com.example.misnotas.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.misnotas.model.Note

class DiffUtilCallbackNotes:DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean { /*El contenido cambió*/
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id==newItem.id
    }
}