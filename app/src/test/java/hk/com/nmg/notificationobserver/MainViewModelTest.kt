package hk.com.nmg.notificationobserver

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify


@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {


    @Mock
    var emailService: EmailServiceInterface = mock()

    @Mock
    var fileWriterWrapper: FileWriterWrapperProtocol = mock()

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
    fun testCSVString() = runTest {

        val list = listOf(
            AppPushModel(
                appName = "App_Name",
                date = "Date",
                receivedTime = "Received_Time",
                appPushTitle = "App Push Title",
                appPushContent = "App Push Content"
            ),
            AppPushModel(
                appName = "App_Name 1",
                date = "Date 1",
                receivedTime = "Received_Time 1",
                appPushTitle = "App Push Title 1",
                appPushContent = "App Push Content 1"
            )
        )

        Assert.assertEquals(list.toCSVString(), """
            App_Name,Date,Received_Time,App Push Title,App Push Content
            App_Name 1,Date 1,Received_Time 1,App Push Title 1,App Push Content 1
        """.trimIndent())
    }

    @Test
    fun sendEmail() = runTest {
        val notificationManager = NotificationManager

        val sut = MainViewModel(notificationManager, emailService = emailService, fileWriterWrapper = fileWriterWrapper)
        sut.viewDidLoad()
        val list = listOf(
            AppPushModel(
                appName = "App_Name",
                date = "Date",
                receivedTime = "Received_Time",
                appPushTitle = "App Push Title",
                appPushContent = "App Push Content"
            )
        )
        notificationManager.rotate()

        notificationManager.send(
            list.first()
        )
        advanceTimeBy(6000)
        verify(emailService, Mockito.times(1)).send(
            any()
        )
        advanceTimeBy(6000)
        verify(fileWriterWrapper, Mockito.times(1)).writeToFile(
            anyString(),
            eq(list.toCSVString())
        )
    }
}