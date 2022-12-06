package com.example.misnotas.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.misnotas.BuildConfig
import com.example.misnotas.R
import com.example.misnotas.activities.MainActivity
import com.example.misnotas.adapters.RvVideoAdapter
import com.example.misnotas.databinding.FragmentVideoBinding
import com.example.misnotas.databinding.VideoItemLayoutBinding
import com.example.misnotas.model.Multimedia
import com.example.misnotas.utils.hideKeyboard
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class VideoFragment:Fragment(R.layout.fragment_video) {
    private lateinit var videoBinding: FragmentVideoBinding
    private lateinit var videoItemBinding: VideoItemLayoutBinding
    private lateinit var rvAdapter: RvVideoAdapter
    private val myCalendar = Calendar.getInstance()
    private val REQUEST_VIDEO_CAPTURE: Int = 1
    lateinit var mediaController: MediaController
    lateinit var videoURI: Uri

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val activity=activity as MainActivity



        exitTransition= MaterialElevationScale(false).apply {
            duration=350
        }
        enterTransition= MaterialElevationScale(true).apply {
            duration=350
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity=activity as MainActivity
        videoBinding= FragmentVideoBinding.bind(view)
        videoItemBinding = VideoItemLayoutBinding.inflate(layoutInflater)

        requireView().hideKeyboard()

        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            // activity.window.statusBarColor= Color.WHITE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor= Color.parseColor("#9E9D9D")
        }



        videoBinding.fabAddVideo.setOnClickListener {

            Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
                takeVideoIntent.resolveActivity(activity.packageManager)?.also {

                    // Create the File where the photo should go
                    val videoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }

                    // Continue only if the File was successfully created
                    videoFile?.also {
                        videoURI = FileProvider.getUriForFile(
                            Objects.requireNonNull(activity.applicationContext),
                            BuildConfig.APPLICATION_ID+".provider",it
                        )
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)

                    }

                }

            }
            addVideo(
                Multimedia(
                    0, 0,2,currentVideoPath, SimpleDateFormat.getInstance().format(myCalendar.time)
                )
            )
        }

        recyclerViewDisplay()

        dataChanged()

        videoBinding.rvVideos.setOnScrollChangeListener{_,scrollX,scrollY,_,oldScrollY ->
            when{
                scrollY > oldScrollY -> {
                    videoBinding.fabTextVideo.isVisible=false
                }
                scrollX==scrollY -> {
                    videoBinding.fabTextVideo.isVisible=true
                }
                else -> {
                    videoBinding.fabTextVideo.isVisible=true
                }
            }
        }
    }


    lateinit var currentVideoPath: String
    @Throws(IOException::class)
    fun createImageFile(): File{
        // Create an image file name

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        // val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val storageDir: File? = activity?.filesDir
        return File.createTempFile(
            "MP4_${timeStamp}_", /* prefix */
            ".mp4", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentVideoPath = absolutePath
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {

            //Carga el video a partir de URI
            videoItemBinding.videoViewItem.setVideoPath(videoURI.path)
          //  videoItemBinding.videoViewItem.start()

            dataChanged()
        }

    }



    private fun dataChanged(){
        rvAdapter.submitList(DataSourceVideo.lstVideo)
        videoBinding.rvVideos.adapter?.notifyDataSetChanged()
    }

    private fun addVideo(video: Multimedia){
        DataSourceVideo.lstVideo.add(video)
        dataChanged()
    }

    private fun recyclerViewDisplay() {
        when(resources.configuration.orientation){
            Configuration.ORIENTATION_PORTRAIT -> setUpRecyclerView(2)
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecyclerView(3)
        }
    }

    /*Usaremos adaptador de lista  -> Actualiza en automatico el contenido despues de hacer un cambio */
    private fun setUpRecyclerView(spanCount: Int) {
        videoBinding.rvVideos.apply {
            layoutManager= StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            rvAdapter= RvVideoAdapter(/*DataSourceVideo.lstVideo*/)
            rvAdapter.stateRestorationPolicy=
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter=rvAdapter
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }
















}