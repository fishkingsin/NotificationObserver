package hk.com.nmg.notificationobserver

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import android.util.Log


class NotificationListener: NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        Log.d("NotificationListener", "onNotificationPosted: $packageName")
        Log.d("NotificationListener", "onNotificationPosted: ${sbn.notification.extras.getCharSequence("android.title")}")
        Log.d("NotificationListener", "onNotificationPosted: ${sbn.notification.extras.getCharSequence("android.text")}")

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Nothing to do
    }
}