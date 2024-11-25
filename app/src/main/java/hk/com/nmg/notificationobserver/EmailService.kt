package hk.com.nmg.notificationobserver

import java.io.File
import java.lang.Error


class EmailService(private val emailServiceListener: EmailServiceListener): EmailServiceInterface {

    data class Email(
        val to: String,
        val tos: List<String>? = null,
        val from: String,
        val subject: String,
        val body: String,
        val attachment: String? = null
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