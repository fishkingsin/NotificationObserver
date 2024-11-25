package hk.com.nmg.notificationobserver

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class EmailInstrumentedTest {

    @Test
    fun dryRun() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val timestamp = System.currentTimeMillis()
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        val instant = Instant.ofEpochMilli(timestamp)

// Adding the timezone information to be able to format it (change accordingly)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        val list = listOf(
            AppPushModel(
                appName = "HK01",
                date = dateFormatter.format(date),
                receivedTime = timeFormatter.format(date),
                appPushTitle = "App Push Title",
                appPushContent = "App Push Content"
            )
        )


        val fileName = "log.csv"
        AmazonSESService(email = EmailService.Email(
            to = BuildConfig.from,
            from = BuildConfig.from,
            subject = "Test",
            body = list.toHtmlTable(),
            attachment = fileName
        )).run(success = {
            println("Email sent")
        }) {
            println("Failed")
        }
    }
}