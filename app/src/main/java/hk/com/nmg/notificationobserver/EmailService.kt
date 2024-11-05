package hk.com.nmg.notificationobserver


class EmailService(private val emailServiceListener: EmailServiceListener): EmailServiceInterface {

    data class Email(
        val to: String,
        val from: String,
        val subject: String,
        val body: String
    )
    override fun send(email: Email) {
        AmazonSESService(email = email).run()
    }

    companion object {
        const val CLIENT_NAME = "Android StackOverflow programmatic email"
    }
}