package hk.com.nmg.notificationobserver

import android.app.NotificationManager
import dev.tools.screenlogger.ScreenLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

object NotificationManager {
    private var _notificationFlow: MutableStateFlow<ScreenLog?> = MutableStateFlow(null)
    val notificationFlow: Flow<ScreenLog>
        get() = _notificationFlow.filterNotNull()

    fun send(screenLog: ScreenLog) {
        _notificationFlow.tryEmit(screenLog)
    }


}