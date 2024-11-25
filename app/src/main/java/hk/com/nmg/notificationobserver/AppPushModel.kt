package hk.com.nmg.notificationobserver

data class AppPushModel(
    val appName: String,
    val date: String,
    val receivedTime: String,
    val appPushTitle: String,
    val appPushContent: String
)


fun  List<AppPushModel>.toCSVString(): String {
    return joinToString(separator = "\n") {
        "${it.appName},${it.date},${it.receivedTime},${it.appPushTitle},${it.appPushContent}"
    }
}