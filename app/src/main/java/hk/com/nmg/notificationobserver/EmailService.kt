package hk.com.nmg.notificationobserver

import java.lang.Error


class EmailService(private val emailServiceListener: EmailServiceListener): EmailServiceInterface {

    data class Email(
        val to: String,
        val from: String,
        val subject: String,
        val body: String
    )
    override fun send(email: Email) {
        AmazonSESService(email = email).run(success = {
            emailServiceListener.onSuccess(null)
        }) {
            emailServiceListener.onFailure(Error(it.message))
        }
    }

    companion object {
        const val CLIENT_NAME = "Android StackOverflow programmatic email"
    }
}