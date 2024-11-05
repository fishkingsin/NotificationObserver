package hk.com.nmg.notificationobserver

import com.google.gson.Gson
import dev.tools.screenlogger.ScreenLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {


    @Mock
    var emailService: EmailServiceInterface = mock()

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()


    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun sendEmail() = runTest {
        val notificationManager = NotificationManager

        val sut = MainViewModel(notificationManager, emailService = emailService)
        val log = listOf(ScreenLog(
            "actionTag",
            "screenLogType",
            "screenLog",
            System.currentTimeMillis()
        ))
        val json = Gson().toJson(log)
        notificationManager.send(
            log.first()
        )
        advanceTimeBy(6000)
        verify(emailService, Mockito.times(1)).send(
            EmailService.Email(
                to = BuildConfig.to,
                from = BuildConfig.from,
                subject = "Notification Observer Logs",
                body = json.toString().jsonToHtmlTable()
            )
        )
    }
}