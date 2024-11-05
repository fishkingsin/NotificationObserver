package hk.com.nmg.notificationobserver

import okhttp3.ResponseBody
import java.io.IOException

interface EmailServiceListener {
    fun onFailure(e: Error)

    fun onSuccess(body: ResponseBody?)

}
