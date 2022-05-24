package com.example.floating_capture.capture

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.floating_capture.R

object NotificationUtils {

    const val NOTIFICATION_ID = 1337

    private const val NOTIFICATION_CHANNEL_ID = "com.mtsahakis.mediaprojectiondemo.app"
    private const val NOTIFICATION_CHANNEL_NAME = "com.mtsahakis.mediaprojectiondemo.app"

    fun getNotification(context: Context): Pair<Int, Notification> {
        createNotificationChannel(context)

        val notification = createNotification(context)
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
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_camera_24)
            .setContentTitle("타이틀")
            .setContentText("내용")
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            //.priority = Notification.PRIORITY_LOW
            .setShowWhen(true)
            .build()
    }

}