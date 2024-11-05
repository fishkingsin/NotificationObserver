package hk.com.nmg.notificationobserver

import android.util.Log
import com.sun.mail.smtp.SMTPTransport
import java.util.Date
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class AmazonSESService(
    // Replace sender@example.com with your "From" address.
    // This address must be verified.
    val FROM: String = BuildConfig.from,
    val FROMNAME: String = "Single Detection",
    // Replace recipient@example.com with a "To" address. If your account
    // is still in the sandbox, this address must be verified.
    val TO: String = BuildConfig.to,
    // Replace smtp_username with your Amazon SES SMTP user name.
    val SMTP_USERNAME: String = BuildConfig.SMTP_USERNAME,
    // Replace smtp_password with your Amazon SES SMTP password.
    val SMTP_PASSWORD: String = BuildConfig.SMTP_PASSWORD,
    // The name of the Configuration Set to use for this message.
    // If you comment out or remove this variable, you will also need to
    // comment out or remove the header below.
    val CONFIGSET: String = "ConfigSet",
    // Amazon SES SMTP host name. This example uses the US West (Oregon) region.
    // See https://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html#region-endpoints
    // for more information.
    val HOST: String = BuildConfig.HOST,
    // The port you will connect to on the Amazon SES SMTP endpoint.
    val SUBJECT: String = "${Date()} Single Detection Report",
    val email: EmailService.Email? = null
) {

    companion object {
        const val PORT: Int = 587
        const val TAG = "AmazonSESService"
    }


    val BODY: String = java.lang.String.join(
        System.getProperty("line.separator"),
        "<h1>Amazon SES SMTP Email Test</h1>",
        "<p>This email was sent with Amazon SES using the ",
        "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
        " for <a href='https://www.java.com'>Java</a>."
    )

    fun run() {
        // Create a Properties object to contain connection configuration information.

        val props: Properties = System.getProperties()
        props["mail.smtp.host"] = HOST
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.port"] = PORT
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.auth"] = "true"

        val session = Session.getDefaultInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD)
            }
        })
        if (BuildConfig.DEBUG) {
            session.debug = true
        }

        // Create a message with the specified information.
        val msg = MimeMessage(session)

        msg.subject = SUBJECT
        if (email != null) {
            msg.setFrom(InternetAddress(email.from))
            msg.setRecipient(Message.RecipientType.TO, InternetAddress(email.to, false))

            msg.setContent(email.body, "text/html")
        } else {
            msg.setFrom(InternetAddress(FROM))
            msg.setRecipient(Message.RecipientType.TO, InternetAddress(TO, false))
            msg.setContent(BODY, "text/html")
        }


        // Add a configuration set header. Comment or delete the
        // next line if you are not using a configuration set
//        msg.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET)

        msg.sentDate = Date()
//        msg.saveChanges()
        // Create a transport.
        val transport: SMTPTransport = session.getTransport() as SMTPTransport


        // Send the message.
        try {
            if (BuildConfig.DEBUG) Log.d(TAG,"Connecting...")


            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect()
            if (BuildConfig.DEBUG) Log.d(TAG,"Sending...")

            // Send the email.
            transport.sendMessage(msg, msg.allRecipients)
            if (BuildConfig.DEBUG) Log.d(TAG,"Email sent!")
            val serverResponse: String = transport.lastServerResponse
            val prefix = "250 Ok "
            if (serverResponse.startsWith(prefix)) {
                val messageId = serverResponse.substring(prefix.length)
                print(messageId)
            }
            transport.close()
        } catch (ex: Exception) {
            if (BuildConfig.DEBUG) Log.d(TAG,"The email was not sent.")
            if (BuildConfig.DEBUG) Log.d(TAG,"Error message: " + ex.message)
            if (BuildConfig.DEBUG) Log.d(TAG,"Error message: " + ex.printStackTrace())
        } finally {
            // Close and terminate the connection.
            transport.close()
        }
    }


}