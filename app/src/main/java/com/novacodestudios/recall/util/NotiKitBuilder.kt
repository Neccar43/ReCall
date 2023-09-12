package com.novacodestudios.recall.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotiKitBuilder(private val context: Context) {

    private var channelId: String? = null
    private var channelName: String? = null
    private var channelDescription: String? = null
    private var intent: Intent? = null
    private var smallIcon: Int = 0
    private var contentTitle: String? = null
    private var contentText: String? = null

    fun setChannel(channelId: String, name: String, description: String): NotiKitBuilder {
        this.channelId = channelId
        this.channelName = name
        this.channelDescription = description
        return this
    }

    fun setIntent(intent: Intent): NotiKitBuilder {
        this.intent = intent
        return this
    }

    fun setSmallIcon(icon: Int): NotiKitBuilder {
        this.smallIcon = icon
        return this
    }

    fun setContent(title: String, text: String): NotiKitBuilder {
        this.contentTitle = title
        this.contentText = text
        return this
    }

    fun build() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 1,
            intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId!!)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(smallIcon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(1, builder.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        var channel: NotificationChannel? = notificationManager.getNotificationChannel(channelId!!)
        if (channel == null) {
            channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.description = channelDescription
            notificationManager.createNotificationChannel(channel)
        }
    }
}
