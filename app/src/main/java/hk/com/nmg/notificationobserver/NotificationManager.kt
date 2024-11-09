package hk.com.nmg.notificationobserver

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

object NotificationManager {
    private var _notificationFlow: MutableStateFlow<AppPushModel?> = MutableStateFlow(null)
    val notificationFlow: Flow<AppPushModel>
        get() = _notificationFlow.filterNotNull()

    fun send(model: AppPushModel) {
        _notificationFlow.tryEmit(model)
    }


}