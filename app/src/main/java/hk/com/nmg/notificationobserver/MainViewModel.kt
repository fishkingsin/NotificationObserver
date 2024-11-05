package hk.com.nmg.notificationobserver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dev.tools.screenlogger.ScreenLog
import hk.com.nmg.notificationobserver.EmailService.Email
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

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
    )) : ViewModel() {
    private val _logs: MutableStateFlow<List<ScreenLog>> = MutableStateFlow(emptyList())
    @OptIn(FlowPreview::class)
    private val logs: Flow<List<ScreenLog>>
        get() = _logs.filter { it ->
            it.isNotEmpty()
        }.debounce(5000)

    init {
        viewModelScope.launch {
            notificationManager.notificationFlow.collect { log ->
                val newList = ArrayList(_logs.value)
                newList.add(log)
                _logs.emit(newList)
            }
        }
        viewModelScope.launch {

            logs.collect { logs ->
                sendEmail(logs)
            }
        }
    }


    private fun sendEmail(logs: List<ScreenLog>) {

        val json = Gson().toJson(logs)
        val body = json.toString().jsonToHtmlTable()
        emailService.send(
            Email(
                to = BuildConfig.to,
                from = BuildConfig.from,
                subject = "Notification Observer Logs",
                body = body
            )
        )
    }




}


