package hk.com.nmg.notificationobserver

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dev.tools.screenlogger.ScreenLoggerHelper
import dev.tools.screenlogger.ScreenLoggerStarter


class OnScreenLogger: OnScreenLoggerInterface {
    override fun startLogging(application: Application, context: Context) {
        ScreenLoggerStarter.getInstance(application)?.startLogging(context)
    }

    override fun stopLogging(application: Application, context: Context) {
        ScreenLoggerStarter.getInstance(application)?.stopLogging(context)
    }


    override fun log(context: Context?,
                     actionTag: String?,
                     screenLogType: String?,
                     screenLog: String?) {
        val broadcastIntent = Intent(ScreenLoggerHelper.ACTION_SCREEN_LOG)
        broadcastIntent.putExtra(ScreenLoggerHelper.KEY_ACTION_TAG, actionTag)
        broadcastIntent.putExtra(ScreenLoggerHelper.KEY_SCREEN_LOG_TYPE, screenLogType)
        broadcastIntent.putExtra(ScreenLoggerHelper.KEY_SCREEN_LOG, screenLog)
        broadcastIntent.putExtra(ScreenLoggerHelper.KEY_TIMESTAMP, System.currentTimeMillis())
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(broadcastIntent)
    }
}