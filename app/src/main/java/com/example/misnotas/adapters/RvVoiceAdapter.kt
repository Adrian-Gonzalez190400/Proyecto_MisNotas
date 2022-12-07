package com.example.misnotas.adapters

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.misnotas.R
import com.example.misnotas.databinding.VoiceItemLayoutBinding
import com.example.misnotas.fragments.DataSourceVoice
import com.example.misnotas.model.Multimedia
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException

class RvVoiceAdapter: ListAdapter<Multimedia, RvVoiceAdapter.VoiceViewHolder>(DiffUtilCallbackVoice()) {
    inner class VoiceViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        private val contenBinding = VoiceItemLayoutBinding.bind(itemView)
        val delete: FloatingActionButton = contenBinding.fabDeleteRecord
        val parent: MaterialCardView = contenBinding.voiceItemLayoutParent
        val record : Button

        init {
            record=itemView.findViewById(R.id.btnPlayRecordVoice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvVoiceAdapter.VoiceViewHolder {
        return VoiceViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.voice_item_layout,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RvVoiceAdapter.VoiceViewHolder, position: Int) {
        getItem(position).let { multimedia ->
            holder.apply {
                parent.transitionName="recyclerView_${multimedia.id}"
                var mStartRecording: Boolean = true
                 var player: MediaPlayer? = null
                //image.setImageURI(multimedia.path.toUri())

                fun stopPlaying() {

                    player?.release()
                    player = null
                }

                fun startPlaying() {
                    player = MediaPlayer().apply {
                        try {
                            setDataSource(multimedia.path)
                            prepare()
                            start()
                        } catch (e: IOException) {}
                    }
                }
                fun onPlay(start: Boolean) =
                    if (start) {
                        startPlaying()
                } else {
                    stopPlaying()
                }

                record.setOnClickListener {
                    onPlay(mStartRecording)
                    record.text=when(mStartRecording){
                        true -> record.context.getString(R.string.stop)
                        false -> record.context.getString(R.string.play)
                    }
                    mStartRecording=!mStartRecording
                }

                delete.setOnClickListener {
                    DataSourceVoice.lstVoice.remove(multimedia)
                    submitList(DataSourceVoice.lstVoice)
                    notifyDataSetChanged()
                }
            }

        }

    }


}