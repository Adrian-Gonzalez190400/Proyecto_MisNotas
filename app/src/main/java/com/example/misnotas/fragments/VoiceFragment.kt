package com.example.misnotas.fragments

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.misnotas.R
import com.example.misnotas.activities.MainActivity
import com.example.misnotas.adapters.RvVoiceAdapter
import com.example.misnotas.databinding.FragmentVoiceBinding
import com.example.misnotas.databinding.VoiceItemLayoutBinding
import com.example.misnotas.model.Multimedia
import com.example.misnotas.utils.hideKeyboard
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

private const val LOG_TAG = "AudioRecordTest"
class VoiceFragment: Fragment(R.layout.fragment_voice) {
    private lateinit var voiceBinding: FragmentVoiceBinding
    private lateinit var voiceItemBinding: VoiceItemLayoutBinding
    lateinit var requestPermissionLauncher : ActivityResultLauncher<String>
    private lateinit var rvAdapter: RvVoiceAdapter
    private var mStartRecording: Boolean = true
    private var fileName: String = ""
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private val myCalendar = Calendar.getInstance()


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
        voiceBinding = FragmentVoiceBinding.bind(view)
        voiceItemBinding = VoiceItemLayoutBinding.inflate(layoutInflater)

        iniPerm()
        val activity = activity as MainActivity
        requireView().hideKeyboard()

        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            // activity.window.statusBarColor= Color.WHITE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor = Color.parseColor("#9E9D9D")
        }

        voiceBinding.fabTextVoiceRecord.setOnClickListener {
            grabar()
            onRecord(mStartRecording)
            voiceBinding.fabTextVoiceRecord.text=when(mStartRecording){
                true -> "Stop recording"
                false -> "Start recording"
            }
            mStartRecording=!mStartRecording
            addVoice(
                Multimedia(
                    0, 0,3,fileName,SimpleDateFormat.getInstance().format(myCalendar.time)
                )
            )
        }
        iniUI()
        recyclerViewDisplay()

        dataChanged()

        voiceItemBinding.btnPlayRecordVoice.setOnClickListener {
            onPlay(mStartRecording)
            voiceItemBinding.btnPlayRecordVoice.text=when(mStartRecording){
                true -> "Stop playing"
                false -> "Start playing"
            }
            mStartRecording = !mStartRecording
            dataChanged()
        }

        voiceBinding.rvRecordings.setOnScrollChangeListener{_,scrollX,scrollY,_,oldScrollY ->
            when{
                scrollY > oldScrollY -> {
                    voiceBinding.fabTextVoiceRecord.isVisible=false
                }
                scrollX==scrollY -> {
                    voiceBinding.fabTextVoiceRecord.isVisible=true
                }
                else -> {
                    voiceBinding.fabTextVoiceRecord.isVisible=true
                }
            }
        }

    }

    private fun iniPerm(){
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
    }

    private fun grabar() {
        revisarPermisos()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun revisarPermisos() {
        val activity=activity as MainActivity
        when {
            ContextCompat.checkSelfPermission(
                activity.applicationContext,
                "android.permission.RECORD_AUDIO"
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.

            }
            shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO") -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                //showInContextUI(...)
                //Toast.makeText(applicationContext, "Debes dar perimso para grabar audios", Toast.LENGTH_SHORT).show()
                MaterialAlertDialogBuilder(activity.applicationContext
                )
                    .setTitle("Title")
                    .setMessage("Debes dar perimso para grabar audios")
                    .setNegativeButton("Cancel") { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton("OK") { dialog, which ->
                        // Respond to positive button press
                        /*requestPermissionLauncher.launch(
                            "android.permission.RECORD_AUDIO")*/

                        // You can directly ask for the permission.
                        requestPermissions(
                            arrayOf("android.permission.RECORD_AUDIO",
                                "android.permission.WRITE_EXTERNAL_STORAGE"),
                            1001)

                    }
                    .show()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                /*requestPermissionLauncher.launch(
                    "android.permission.RECORD_AUDIO")*/
                requestPermissions(
                    arrayOf("android.permission.RECORD_AUDIO",
                        "android.permission.WRITE_EXTERNAL_STORAGE"),
                    1001)
            }
        }
    }

    var mStartPlaying = true
    private fun onRecord(start: Boolean) =
        if (start) {
            iniciarGraabacion()
        } else {
            stopRecording()
        }

    private fun iniciarGraabacion() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            createAudioFile()
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    private fun iniUI() {
        voiceBinding.fabTextVoiceRecord.text = "Start recording"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED
                            )) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    iniciarGraabacion()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    @Throws(IOException::class)
    fun createAudioFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        //val storageDir: File? = filesDir
        return File.createTempFile(
            "AUDIO_${timeStamp}_", /* prefix */
            ".mp3", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            fileName = absolutePath

        }
    }

    private fun dataChanged(){
        rvAdapter.submitList(DataSourceVoice.lstVoice)
        voiceBinding.rvRecordings.adapter?.notifyDataSetChanged()
    }

    private fun addVoice(voice: Multimedia){
        DataSourceVoice.lstVoice.add(voice)
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
        voiceBinding.rvRecordings.apply {
            layoutManager= LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            rvAdapter= RvVoiceAdapter(/*DataSourceImage.lstImage*/)
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