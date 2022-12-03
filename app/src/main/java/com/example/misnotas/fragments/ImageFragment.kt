package com.example.misnotas.fragments

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.MediaController
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.misnotas.BuildConfig
import com.example.misnotas.R
import com.example.misnotas.activities.MainActivity
import com.example.misnotas.adapters.RvImageAdapter
import com.example.misnotas.databinding.FragmentImageBinding
import com.example.misnotas.databinding.ImageItemLayoutBinding
import com.example.misnotas.databinding.ImageItemLayoutBindingImpl
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

class ImageFragment: Fragment(R.layout.fragment_image) {
    private lateinit var imageBinding: FragmentImageBinding
    private lateinit var imageItemBinding: ImageItemLayoutBinding
    private lateinit var rvAdapter: RvImageAdapter
    private val myCalendar = Calendar.getInstance()
    private val REQUEST_VIDEO_CAPTURE: Int = 1001
    lateinit var currentVideoPath: String
    lateinit var photoURI: Uri
    private val REQUEST_IMAGE_CAPTURE: Int = 1000
    lateinit var mediaController: MediaController


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        exitTransition= MaterialElevationScale(false).apply {
            duration=350
        }
        enterTransition= MaterialElevationScale(true).apply {
            duration=350
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageBinding= FragmentImageBinding.bind(view)
        imageItemBinding= ImageItemLayoutBinding.inflate(layoutInflater)


        val activity=activity as MainActivity
        requireView().hideKeyboard()

        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            // activity.window.statusBarColor= Color.WHITE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor= Color.parseColor("#9E9D9D")
        }



        imageBinding.fabAddImage.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(activity.packageManager)?.also {

                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }

                    // Continue only if the File was successfully created
                    photoFile?.also {
                        photoURI = FileProvider.getUriForFile(
                            Objects.requireNonNull(activity.applicationContext),
                            BuildConfig.APPLICATION_ID+".provider",it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }

            }
            addImage(
                Multimedia(
                    0, 0,1,currentPhotoPath,SimpleDateFormat.getInstance().format(myCalendar.time)
                )
            )
        }



        recyclerViewDisplay()

        dataChanged()

        imageBinding.rvImages.setOnScrollChangeListener{_,scrollX,scrollY,_,oldScrollY ->
            when{
                scrollY > oldScrollY -> {
                    imageBinding.fabTextImage.isVisible=false
                }
                scrollX==scrollY -> {
                    imageBinding.fabTextImage.isVisible=true
                }
                else -> {
                    imageBinding.fabTextImage.isVisible=true
                }
            }
        }
    }

    lateinit var currentPhotoPath: String
    @Throws(IOException::class)
    fun createImageFile(): File{
        // Create an image file name

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
       // val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val storageDir: File? = activity?.filesDir
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {

            //Carga la imagen a partir de URI
            imageItemBinding.imageViewFotoMiniatura.setImageURI(
                photoURI
            )

        }

    }



    private fun dataChanged(){
        rvAdapter.submitList(DataSourceImage.lstImage)
        imageBinding.rvImages.adapter?.notifyDataSetChanged()
    }

    private fun addImage(img: Multimedia){
        DataSourceImage.lstImage.add(img)
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
        imageBinding.rvImages.apply {
            layoutManager= LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            rvAdapter= RvImageAdapter(/*DataSourceImage.lstImage*/)
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