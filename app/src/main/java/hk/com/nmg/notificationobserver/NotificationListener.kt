package hk.com.nmg.notificationobserver


import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import dev.tools.screenlogger.ScreenLoggerHelper.SCREEN_LOG_TYPE_TRACKING
import hk.com.nmg.notificationobserver.NotificationManager.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class NotificationListener() :
    NotificationListenerService() {
    private val TAG: String = "NotificationListener"



    override fun onNotificationPosted(sbn: StatusBarNotification) {
        sbn.notification.extras.keySet().forEach {
            if (BuildConfig.DEBUG) Log.d("NotificationListener", "onNotificationPosted: $it")
        }
        val bundle = sbn.notification.extras
        val lines: Array<out CharSequence>? = bundle.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
        var temp = ""
        if (lines != null) {
            for (line in lines) {
                temp = "line: $line\n"
            }
        }
        Log.d(TAG, "temp $temp")
        val message = sbn.notification.extras.keySet().map {
            try {
                if (BuildConfig.DEBUG) Log.d(TAG, "sbn.notification.extras.get(it) ${sbn.notification.extras.get(it)}")
                return@map sbn.notification.extras.getCharSequence(it)
            } catch (e: Exception) {
               return@map ""
            }
        }.filter { it.isNullOrBlank() }

        val screenLog = "${sbn.notification.extras.getCharSequence("android.title")} ${
            sbn.notification.extras.getCharSequence("android.text")
        } $message"
        val packageName = sbn.packageName
        if (packageName.contains("com.google.android")) {
            if (BuildConfig.DEBUG) Log.d(TAG, "filtered $packageName")
            return
        }
        OnScreenLogger().log(
            this,
            packageName,
            SCREEN_LOG_TYPE_TRACKING,
            screenLog
        )


        if (BuildConfig.DEBUG) Log.d(
            "NotificationListener",
            "onNotificationPosted: $packageName title ${sbn.notification.extras.getCharSequence("android.title")}"
        )
        if (BuildConfig.DEBUG) Log.d(
            "NotificationListener",
            "onNotificationPosted: ${sbn.notification.extras.getCharSequence("android.text")}"
        )
        val timestamp = System.currentTimeMillis()
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        val instant = Instant.ofEpochMilli(timestamp)

// Adding the timezone information to be able to format it (change accordingly)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        CoroutineScope(Dispatchers.IO).launch {
            send(
                AppPushModel(
                    appName = packageName,
                    date = dateFormatter.format(date),
                    receivedTime = timeFormatter.format(date),
                    appPushTitle = sbn.notification.extras.getCharSequence("android.title")
                        .toString(),
                    appPushContent = sbn.notification.extras.getCharSequence("android.text")
                        .toString()
                )
            )
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Nothing to do
    }
}

