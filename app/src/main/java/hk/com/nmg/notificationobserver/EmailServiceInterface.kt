package hk.com.nmg.notificationobserver

import hk.com.nmg.notificationobserver.EmailService.Email

interface EmailServiceInterface {
    fun send(email: Email)
}
