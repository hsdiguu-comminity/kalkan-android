package com.kalkan.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kalkan.app.MainActivity
import com.kalkan.app.R

/**
 * TR: Umut — Kalkan bildirim kanallarını yöneten ve güvenlik uyarı bildirimleri gösteren yardımcı sınıf.
 * EN: Umut — Helper class managing Kalkan notification channels and displaying security alert notifications.
 */
object NotificationHelper {

    const val CHANNEL_SECURITY_ALERTS = "kalkan_security_alerts"
    const val CHANNEL_REPORT_UPDATES = "kalkan_report_updates"

    /**
     * TR: Android 8.0+ (API 26+) için gerekli bildirim kanallarını oluşturur.
     * EN: Creates required notification channels for Android 8.0+ (API 26+).
     */
    fun createNotificationChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        val securityChannel = NotificationChannel(
            CHANNEL_SECURITY_ALERTS,
            context.getString(R.string.notification_channel_security_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notification_channel_security_desc)
            enableVibration(true)
        }

        val reportChannel = NotificationChannel(
            CHANNEL_REPORT_UPDATES,
            context.getString(R.string.notification_channel_reports_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notification_channel_reports_desc)
        }

        manager.createNotificationChannels(listOf(securityChannel, reportChannel))
    }

    /**
     * TR: POST_NOTIFICATIONS izni varsa kullanıcıya güvenlik veya ihbar durumu bildirimi gösterir.
     * EN: Displays a security or report status notification if POST_NOTIFICATIONS permission is granted.
     */
    fun showNotification(
        context: Context,
        title: String,
        body: String,
        channelId: String = CHANNEL_SECURITY_ALERTS,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        createNotificationChannels(context)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}