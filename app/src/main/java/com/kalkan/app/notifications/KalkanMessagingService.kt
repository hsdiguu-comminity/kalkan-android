package com.kalkan.app.notifications

import android.content.Context
import com.kalkan.app.R

/**
 * TR: Umut — Push bildirim (FCM) yüklerini işleyen ve yerel bildirimlere yönlendiren servis katmanı.
 *     Kullanıcı-1 (Alp) libs.versions.toml içine firebase-messaging eklediğinde FirebaseMessagingService
 *     doğrudan bu işleyiciyi tetikler.
 * EN: Umut — Push notification payload handler delegating incoming alerts to NotificationHelper.
 */
class KalkanMessagingService {

    /**
     * TR: Yeni FCM cihaz token'ı üretildiğinde backend'e kaydedilmek üzere işler.
     * EN: Handles newly generated FCM device tokens for backend registration.
     */
    fun onNewTokenReceived(context: Context, token: String) {
        if (token.isBlank()) return
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PENDING_FCM_TOKEN, token).apply()
    }

    /**
     * TR: Gelen push bildirim verisini (başlık, içerik ve tür) ayrıştırıp ilgili bildirim kanalına yönlendirir.
     * EN: Parses incoming push payload (title, body, type) and routes it to the proper notification channel.
     */
    fun handleIncomingMessage(
        context: Context,
        title: String?,
        body: String?,
        data: Map<String, String> = emptyMap()
    ) {
        val resolvedTitle = title?.takeIf { it.isNotBlank() }
            ?: data["title"]?.takeIf { it.isNotBlank() }
            ?: context.getString(R.string.notification_default_title)

        val resolvedBody = body?.takeIf { it.isNotBlank() }
            ?: data["body"]?.takeIf { it.isNotBlank() }
            ?: context.getString(R.string.notification_default_body)

        val type = data["type"]?.lowercase()
        val channelId = if (type == "report_update") {
            NotificationHelper.CHANNEL_REPORT_UPDATES
        } else {
            NotificationHelper.CHANNEL_SECURITY_ALERTS
        }

        NotificationHelper.showNotification(
            context = context,
            title = resolvedTitle,
            body = resolvedBody,
            channelId = channelId
        )
    }

    companion object {
        private const val PREFS_NAME = "kalkan_push_prefs"
        private const val KEY_PENDING_FCM_TOKEN = "pending_fcm_token"

        fun getSavedFcmToken(context: Context): String? {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_PENDING_FCM_TOKEN, null)
        }
    }
}