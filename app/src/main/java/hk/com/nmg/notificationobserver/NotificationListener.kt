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


class NotificationListener() :
    NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val screenLog = "${sbn.notification.extras.getCharSequence("android.title")} ${
            sbn.notification.extras.getCharSequence("android.text")
        }"
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
        NotificationManager.send(
            ScreenLog(
                ScreenLoggerHelper.ACTION_SCREEN_LOG,
                SCREEN_LOG_TYPE_TRACKING,
                screenLog,
                System.currentTimeMillis()
            )
        )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Nothing to do
    }
}

