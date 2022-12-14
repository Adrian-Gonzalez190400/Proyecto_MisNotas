package com.example.misnotas.activities
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.example.misnotas.R
import com.example.misnotas.databinding.ActivityMainBinding
import com.example.misnotas.db.NoteDatabase
import com.example.misnotas.notifications.NotificationReceiver
import com.example.misnotas.repository.MultimediaRepository
import com.example.misnotas.repository.NoteRepository
import com.example.misnotas.repository.ReminderRepository
import com.example.misnotas.viewModel.*

class MainActivity : AppCompatActivity() {

    lateinit var noteActivityViewModel: NoteActivityViewModel
    lateinit var reminderActivityViewModel: ReminderActivityViewModel
    lateinit var multimediaActivityViewModel: MultimediaActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)

        try {
            setContentView(binding.root)
            val noteRepository=NoteRepository(NoteDatabase(this))
            val reminderRepository=ReminderRepository(NoteDatabase(this))
            val multimediaRepository=MultimediaRepository(NoteDatabase(this))
            val noteActivityViewModelFactory=NoteActivityViewModelFactory(noteRepository, reminderRepository,multimediaRepository)
            val reminderActivityViewModelFactory=ReminderActivityViewModelFactory(reminderRepository)
            val multimediaActivityViewModelFactory=MultimediaActivityViewModelFactory(multimediaRepository)

//            val receiver = ComponentName(applicationContext, NotificationReceiver::class.java)
//
//            applicationContext.packageManager.setComponentEnabledSetting(
//                receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP
//            )

            noteActivityViewModel=ViewModelProvider(this,
                noteActivityViewModelFactory)[NoteActivityViewModel::class.java]

            reminderActivityViewModel=ViewModelProvider(this,
            reminderActivityViewModelFactory)[ReminderActivityViewModel::class.java]

            multimediaActivityViewModel=ViewModelProvider(this,
            multimediaActivityViewModelFactory)[MultimediaActivityViewModel::class.java]
        }catch (e: Exception){
            Log.d("TAG","Error")
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
               R.id.notaFragment,
               R.id.nota_SimpleFragment,
               R.id.taskFragment
            ),
            binding.drawerLayout
        )

        val navHostFragment=supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        navController=navHostFragment.findNavController()

        //Toolbar
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        //DraweLayout
        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}