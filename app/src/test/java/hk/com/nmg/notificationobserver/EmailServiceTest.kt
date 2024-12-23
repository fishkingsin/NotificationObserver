package hk.com.nmg.notificationobserver


import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RunWith(MockitoJUnitRunner::class)
class EmailServiceTest {
    @Mock
    var emailServiceListener: EmailServiceListener = mock()
    @Test
    fun send() {
        val timestamp = System.currentTimeMillis()
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        val instant = Instant.ofEpochMilli(timestamp)

// Adding the timezone information to be able to format it (change accordingly)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val sut = EmailService(emailServiceListener)
        val list = listOf(
            AppPushModel(
                appName = "HK01",
                date = dateFormatter.format(date),
                receivedTime = timeFormatter.format(date),
                appPushTitle = "App Push Title",
                appPushContent = "App Push Content"
            )
        )
        val fileName = "test.csv"
        sut.send(EmailService.Email(
            to = BuildConfig.from,
            tos = BuildConfig.to.split(",").toList(),
            from = BuildConfig.from,
            subject = "Test", body = list.toHtmlTable(), attachment = fileName
        ))

        verify(emailServiceListener, Mockito.times(1)).onSuccess(null)

    }
}