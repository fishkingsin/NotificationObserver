package hk.com.nmg.notificationobserver


import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class EmailServiceTest {
    @Mock
    var emailServiceListener: EmailServiceListener = mock()
    @Test
    fun send() {
        val sut = EmailService(emailServiceListener)
        sut.send(EmailService.Email(
            to = BuildConfig.to,
            from = BuildConfig.from,
            "Test", "Test"))

        verify(emailServiceListener, Mockito.times(1)).onSuccess(any())

    }
}