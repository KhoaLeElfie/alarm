package com.gdelataillade.alarm.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.net.Uri
import android.media.AudioAttributes
import androidx.core.app.NotificationCompat

class NotificationHandler(private val context: Context) {
    companion object {
        private const val CHANNEL_ID = "alarm_plugin_channel"
        private const val CHANNEL_NAME = "Alarm Notification"
    }

    init {
        createNotificationChannel()
    }

   private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(null, null)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildNotification(title: String, body: String, fullScreen: Boolean, pendingIntent: PendingIntent): Notification {
        val iconResId = context.resources.getIdentifier("ic_launcher", "mipmap", context.packageName)
        val largeIcon = BitmapFactory.decodeResource(context.resources, iconResId)
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val notificationPendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID) // For API 26 and above
        } else {
            Notification.Builder(context) // For lower API levels
        }

        notificationBuilder
            .setSmallIcon(iconResId)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setOngoing(true)
            .setContentIntent(notificationPendingIntent)
            .setSound(null)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationBuilder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
        }

        if (fullScreen) {
            notificationBuilder.setFullScreenIntent(notificationPendingIntent, true)
        }

        return notificationBuilder.build()
    }
}