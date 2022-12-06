package com.example.misnotas.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.misnotas.R
import com.example.misnotas.databinding.VideoItemLayoutBinding
import com.example.misnotas.fragments.DataSourceVideo
import com.example.misnotas.model.Multimedia
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class RvVideoAdapter: ListAdapter<Multimedia,RvVideoAdapter.VideoViewHolder>(DiffUtilCallbackVideo()) {
    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contenBinding = VideoItemLayoutBinding.bind(itemView)
        val delete: FloatingActionButton = contenBinding.fabDeleteVideo
        val parent: MaterialCardView = contenBinding.videoItemLayoutParent
        val video : VideoView

        init {
            video=itemView.findViewById(R.id.videoViewItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.video_item_layout,parent,false)
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        getItem(position).let { multimedia ->
            holder.apply {
                parent.transitionName="recyclerView_${multimedia.id}"
                video.setVideoPath(multimedia.path)
                video.start()
                /*MediaController*/
                var mediaController = MediaController(video.context)
                mediaController.setAnchorView(video)
                video.setMediaController(mediaController)
               // video.setOnClickListener { mediaController.show() }
                //mediaController= MediaController(activity?.applicationContext)


                delete.setOnClickListener {
                    DataSourceVideo.lstVideo.remove(multimedia)
                    submitList(DataSourceVideo.lstVideo)
                    notifyDataSetChanged()
                }
            }

        }
    }
}