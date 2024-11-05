package hk.com.nmg.notificationobserver

import com.google.gson.Gson
import dev.tools.screenlogger.ScreenLog
import org.junit.Test

class AmazonSESServiceTest {

    @Test
    fun run() {
        val log = listOf(
            ScreenLog(
            "actionTag",
            "screenLogType",
            "screenLog",
            System.currentTimeMillis()
        )
        )
        val json = Gson().toJson(log)

        AmazonSESService(email = EmailService.Email(
            to = BuildConfig.to,
            from = BuildConfig.from,
            subject = "Test",
            body = json.toString().jsonToHtmlTable()
        )).run()
    }
}