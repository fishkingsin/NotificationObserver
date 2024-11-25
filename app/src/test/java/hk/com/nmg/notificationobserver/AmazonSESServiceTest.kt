package hk.com.nmg.notificationobserver

import org.junit.Test
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AmazonSESServiceTest {

    @Test
    fun run() {
        // require vpn

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


        AmazonSESService(email = EmailService.Email(
            to = BuildConfig.from,
            from = BuildConfig.from,
            subject = "Test",
            body = list.toHtmlTable(),
            attachment = "test.csv"
        )).run(success = {
            println("Email sent")
        }) {
            println("Failed")
        }
    }
}