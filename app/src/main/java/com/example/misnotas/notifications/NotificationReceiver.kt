package com.example.misnotas.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.misnotas.activities.MainActivity


const val channelID = "TASK-REMINDERS-CHANNEL"
class NotificationReceiver : BroadcastReceiver (){
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { createNotificationChannel(it, null) }

//        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
//            // Set the alarms here.
//            showNotification(context, intent)
//        } else{
//            showNotification(context, intent)
//        }

        val notificationID = intent?.getIntExtra("notificationId", 0)

        val intentTap = Intent(context, MainActivity::class.java)
        intentTap.putExtra("notificationId", notificationID)
        intentTap.flags = Intent.FLAG_ACTIVITY_MULTIPLE_TASK

        var pendingIntent = PendingIntent.getActivity(
            context,
            notificationID!!, intentTap,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context!!, channelID)
            .setSmallIcon(com.google.android.material.R.drawable.ic_clock_black_24dp)
            .setContentTitle(intent?.getStringExtra("titleExtra"))
            .setContentText(intent?.getStringExtra("messageExtra"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID!!, notification)
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