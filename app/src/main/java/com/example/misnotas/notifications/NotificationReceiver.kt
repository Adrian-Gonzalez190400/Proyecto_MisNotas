package com.example.misnotas.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.misnotas.R
import com.example.misnotas.activities.MainActivity

var notificationID = 0
const val channelID = "TASK-REMINDERS-CHANNEL"
class NotificationReceiver : BroadcastReceiver (){
    override fun onReceive(context: Context?, intent: Intent?) {
        val intentTap = Intent(context, MainActivity::class.java)
        intentTap.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        //todo: hacer que al hacer clic se abra main activity
        //todo: se puede mandar el notificationId como extra y recuperar la tarea de la que viene
//        intentTap.putExtra("idTarea", 1002)
//        val pendingIntent = PendingIntent.getActivity(
//            context, 0,
//            intentTap, PendingIntent.FLAG_UPDATE_CURRENT
//        )

        context?.let { createNotificationChannel(it, null) }

        val notification = NotificationCompat.Builder(context!!, channelID)
            .setSmallIcon(com.google.android.material.R.drawable.ic_clock_black_24dp)
            .setContentTitle(intent?.getStringExtra("titleExtra"))
            .setContentText(intent?.getStringExtra("messageExtra"))
            //.setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }

    companion object{
        fun createNotificationChannel(context: Context, intent: Intent?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Notification Channel"
                val description = "Task reminders"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(channelID, name, importance)
                channel.description = description
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}