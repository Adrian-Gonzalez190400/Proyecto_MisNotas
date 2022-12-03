package com.example.misnotas.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.misnotas.R
import com.example.misnotas.databinding.ImageItemLayoutBinding
import com.example.misnotas.fragments.DataSourceImage
import com.example.misnotas.model.Multimedia
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class RvImageAdapter: ListAdapter<Multimedia, RvImageAdapter.ImageViewHolder>(DiffUtilCallbackImage()) {
    inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val contenBinding= ImageItemLayoutBinding.bind(itemView)
        val delete: FloatingActionButton =contenBinding.fabDeleteImage
        val parent: MaterialCardView =contenBinding.imageItemLayoutParent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item_layout,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        getItem(position).let { multimedia ->
            holder.apply {
                parent.transitionName="recyclerView_${multimedia.id}"

                delete.setOnClickListener {
                    DataSourceImage.lstImage.remove(multimedia)
                    submitList(DataSourceImage.lstImage)
                    notifyDataSetChanged()
                }
            }

        }
    }
}