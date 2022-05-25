package com.example.floating_capture.capture

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.floating_capture.MainActivity
import com.example.floating_capture.R


object NotificationUtils {

    const val NOTIFICATION_ID = 1337

    private const val NOTIFICATION_CHANNEL_ID = "channel_id"
    private const val NOTIFICATION_CHANNEL_NAME = "chnnael_name"

    fun getNotification(context: Context): Pair<Int, Notification> {
        createNotificationChannel(context)

        val notification = createNotification(context)
        notification.flags = Notification.FLAG_INSISTENT or Notification.FLAG_AUTO_CANCEL
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)

        return Pair(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(context: Context): Notification {
        val intent = Intent(
            context, MainActivity::class.java
        ).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_camera_24)
            .setContentTitle("타이틀")
            .setContentText("내용")
            //.setOngoing(false) //prevent noti remove
            .setCategory(Notification.CATEGORY_SERVICE)
            .setAutoCancel(true) // do not work if setContentIntent(PendingIntent())
            //.priority = Notification.PRIORITY_LOW
            //.setShowWhen(true)
            .setContentIntent(pendingIntent)
            .build()
    }

}