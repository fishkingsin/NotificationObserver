package hk.com.nmg.notificationobserver

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hk.com.nmg.notificationobserver.EmailService.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.cancellation.CancellationException

fun <A, B: Any, R> Flow<A>.withLatestFrom(other: Flow<B>, transform: suspend (A, B) -> R): Flow<R> = flow {
    coroutineScope {
        val latestB = AtomicReference<B?>()
        val outerScope = this
        launch {
            try {
                other.collect { latestB.set(it) }
            } catch(e: CancellationException) {
                outerScope.cancel(e) // cancel outer scope on cancellation exception, too
            }
        }
        collect { a: A ->
            latestB.get()?.let { b -> emit(transform(a, b)) }
        }
    }
}

class MainViewModel(
    private val notificationManager: NotificationManager = NotificationManager,
    private val emailServiceListener: EmailServiceListener = object : EmailServiceListener {
        override fun onFailure(e: Error) {

        }

        override fun onSuccess(body: ResponseBody?) {

        }
    },
    private val emailService: EmailServiceInterface = EmailService(
        emailServiceListener = emailServiceListener
    ),
    private val fileWriterWrapper: FileWriterWrapperProtocol = FileWriterWrapper()
    ) : ViewModel() {

    private val TAG: String = "MainViewModel"
    private val _log: MutableStateFlow<AppPushModel?> = MutableStateFlow(null)
    private val _logs: MutableStateFlow<List<AppPushModel>> = MutableStateFlow(emptyList())
    @OptIn(FlowPreview::class)
    private val logs: Flow<List<AppPushModel>>
        get() = _logs.filter { it ->
            it.isNotEmpty()
        }.debounce(10000)

    fun viewDidLoad() {
        viewModelScope.launch {
            NotificationManager.init()
        }

        viewModelScope.launch {
            notificationManager.notificationFlow.collect { log ->
                val newList = ArrayList(_logs.value)
                newList.add(log)
                _logs.emit(newList)
            }
        }

        viewModelScope.launch {

            logs.withLatestFrom(NotificationManager.fileName) {
                logs, fileName -> fileName to logs
            }.collect { pair ->
                val fileName = pair.first
                val logs = pair.second
                writeCSV(fileName, logs)
                _logs.emit(emptyList())
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            notificationManager.emitEmailSend.collect { fileName ->
                Log.d(TAG, "emitEmailSend: $fileName")
                sendEmail(absolutePath(fileName))
            }
        }

    }

    private fun sendEmail(fileName: String) {
        Log.d(TAG, "sendEmail: $fileName")
        val tos = BuildConfig.to.split(",").toList()
        emailService.send(
            Email(
                to = BuildConfig.to,
                tos = tos,
                from = BuildConfig.from,
                subject = "Notification Observer Logs",
                body = fileName,
                attachment =  fileName
            )
        )
    }

    private fun absolutePath(fileName: String): String {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        return file.absolutePath
    }
    private fun writeCSV(fileName: String?, logs: List<AppPushModel>) = viewModelScope.launch(Dispatchers.IO) {
        Log.d(TAG, "writeCSV: $fileName")
        fileName?.let { fileWriterWrapper.writeToFile(it, logs.toCSVString() + "\n") }
    }
}

private fun AppPushModel.toBundle(): Bundle {
    return Bundle().apply {
        putString("App_Name", appName)
        putString("Date", date)
        putString("Received_Time", receivedTime)
        putString("App_Push_Title", appPushTitle)
        putString("App_Push_Content", appPushContent)
    }

}


