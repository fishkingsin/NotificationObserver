package hk.com.nmg.notificationobserver

data class AppPushModel(
    val appName: String,
    val date: String,
    val receivedTime: String,
    val appPushTitle: String,
    val appPushContent: String
)
