package com.example.misnotas.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.misnotas.model.Reminder

class DiffUtilCallbackReminder: DiffUtil.ItemCallback<Reminder>()  {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean { /*El contenido cambió*/
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id==newItem.id
    }
}