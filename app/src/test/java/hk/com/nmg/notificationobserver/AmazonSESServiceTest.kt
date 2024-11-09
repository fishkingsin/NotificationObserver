package hk.com.nmg.notificationobserver

import android.util.Log
import com.google.gson.Gson
import dev.tools.screenlogger.ScreenLog
import org.junit.Test

class AmazonSESServiceTest {

    @Test
    fun run() {
        val list = listOf(
            AppPushModel(
                appName = "App Name",
                date = "Date",
                receivedTime = "Received Time",
                appPushTitle = "App Push Title",
                appPushContent = "App Push Content"
            )
        )


        AmazonSESService(email = EmailService.Email(
            to = BuildConfig.from,
            from = BuildConfig.from,
            subject = "Test",
            body = list.toHtmlTable()
        )).run(success = {
            println("Email sent")
        }) {
            println("Failed")
        }
    }
}