package hk.com.nmg.notificationobserver

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object NotificationManager {
    private var _localFileName: String = timestamp()
    private var _fileName: MutableStateFlow<String?> = MutableStateFlow(null)
    val fileName: Flow<String>
        get() = _fileName.filterNotNull()
    private var _emitEmailSend: MutableStateFlow<String?> = MutableStateFlow(null)
    val emitEmailSend: Flow<String>
        get() = _emitEmailSend.filterNotNull()
    private var _notificationFlow: MutableStateFlow<AppPushModel?> = MutableStateFlow(null)
    val notificationFlow: Flow<AppPushModel>
        get() = _notificationFlow.filterNotNull()

    suspend fun init() {
        _localFileName = timestamp()
        _fileName.emit(_localFileName)
    }
    suspend fun send(model: AppPushModel) {
        _notificationFlow.emit(model)
    }

    suspend fun rotate() {
        _emitEmailSend.emit(_localFileName)

        _localFileName = timestamp()
        _fileName.emit(_localFileName)

    }

    fun timestamp(): String {
        val timestamp = System.currentTimeMillis()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        val instant = Instant.ofEpochMilli(timestamp)
        val date = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Hong_Kong"))
        return "report_${dateFormatter.format(date)}.csv"
    }

}