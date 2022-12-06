package com.example.misnotas.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.misnotas.model.Multimedia

class DiffUtilCallbackVoice: DiffUtil.ItemCallback<Multimedia>() {
    override fun areItemsTheSame(oldItem: Multimedia, newItem: Multimedia): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: Multimedia, newItem: Multimedia): Boolean {
        return oldItem.id==newItem.id
    }
}