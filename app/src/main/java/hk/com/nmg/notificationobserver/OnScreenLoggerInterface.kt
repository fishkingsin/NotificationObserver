package hk.com.nmg.notificationobserver

import android.app.Application
import android.content.Context

interface OnScreenLoggerInterface {
    fun startLogging(application: Application, context: Context)

    fun stopLogging(application: Application, context: Context)

    fun log(context: Context?,
            actionTag: String?,
            screenLogType: String?,
            screenLog: String?)
}