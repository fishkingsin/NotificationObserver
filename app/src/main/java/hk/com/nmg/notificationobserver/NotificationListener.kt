package hk.com.nmg.notificationobserver


import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import android.util.Log
import dev.tools.screenlogger.ScreenLog
import dev.tools.screenlogger.ScreenLoggerHelper
import dev.tools.screenlogger.ScreenLoggerHelper.SCREEN_LOG_TYPE_REQUEST
import dev.tools.screenlogger.ScreenLoggerHelper.SCREEN_LOG_TYPE_TRACKING
import hk.com.nmg.notificationobserver.NotificationManager.send
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.HashMap


class NotificationListener() :
    NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        sbn.notification.extras.keySet().forEach {
            Log.d("NotificationListener", "onNotificationPosted: $it")
        }
        val message = sbn.notification.extras.keySet().map { sbn.notification.extras.getCharSequence(it) }

        val screenLog = "${sbn.notification.extras.getCharSequence("android.title")} ${
            sbn.notification.extras.getCharSequence("android.text")
        } $message"
        val packageName = sbn.packageName
        OnScreenLogger().log(
            this,
            packageName,
            SCREEN_LOG_TYPE_TRACKING,
            screenLog
        )


        Log.d(
            "NotificationListener",
            "onNotificationPosted: $packageName title ${sbn.notification.extras.getCharSequence("android.title")}"
        )
        Log.d(
            "NotificationListener",
            "onNotificationPosted: ${sbn.notification.extras.getCharSequence("android.text")}"
        )
        val timestamp = System.currentTimeMillis()
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        val instant = Instant.ofEpochMilli(timestamp)

// Adding the timezone information to be able to format it (change accordingly)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        send(
            AppPushModel(
                appName = packageName,
                date = dateFormatter.format(date),
                receivedTime = timeFormatter.format(date),
                appPushTitle = sbn.notification.extras.getCharSequence("android.title").toString(),
                appPushContent = sbn.notification.extras.getCharSequence("android.text").toString()
            )
        )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Nothing to do
    }
}

