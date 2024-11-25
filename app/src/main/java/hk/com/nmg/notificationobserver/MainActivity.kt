package hk.com.nmg.notificationobserver

import android.Manifest
import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import hk.com.nmg.notificationobserver.ui.theme.NotificationObserverTheme
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    private val TAG: String = "MainActivity"
    private val CHANNEL_ID = "1"
    val viewModel by viewModels<MainViewModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScheduleJobManager(this).scheduleJob()
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

        OnScreenLogger().startLogging(application, this)
        enableEdgeToEdge()
        setContent {
            NotificationObserverTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        val bundle = Bundle()
        bundle.putString("url", "https://www.google.com")
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("textTitle")
            .setContentText("textContent")
            .setExtras(bundle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                // ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                //                                        grantResults: IntArray)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return@with
            }
            // notificationId is a unique int for each notification that you must define.
            val NOTIFICATION_ID = 0
            notify(NOTIFICATION_ID, builder.build())
        }

        viewModel.viewDidLoad()
        if (BuildConfig.DEBUG) {

            val timestamp = System.currentTimeMillis()
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            val instant = Instant.ofEpochMilli(timestamp)

            // Adding the timezone information to be able to format it (change accordingly)
            val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            viewModel.viewModelScope.launch {
                NotificationManager.send(
                    AppPushModel(
                        appName = "HK01",
                        date = dateFormatter.format(date),
                        receivedTime = timeFormatter.format(date),
                        appPushTitle = "App Push Title",
                        appPushContent = "App Push Content"
                    )
                )
            }

        }
    }


}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotificationObserverTheme {
        Greeting("Android")
    }
}

class ScheduleJobManager(private val context: Context) {
    private val TAG: String = "ScheduleJobManager"

    fun scheduleJob() {
        val jobScheduler = context.getSystemService(
            Context.JOB_SCHEDULER_SERVICE
        ) as JobScheduler

        // The JobService that we want to run
        val name: ComponentName = ComponentName(context, RotationJobService::class.java)

        // Schedule the job
        val result = jobScheduler.schedule(getJobInfo(
            // Unique job ID for this job
            UUID.randomUUID().hashCode(),
            // Run every Week
            1,
            name
        ))

        // If successfully scheduled, log this thing
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Scheduled job successfully!")
        }
    }
    @SuppressLint("MissingPermission")
    private fun getJobInfo(id: Int, week: Long, name: ComponentName): JobInfo {
        val interval: Long = if (BuildConfig.DEBUG)
            TimeUnit.HOURS.toMillis(week)
        else TimeUnit.DAYS.toMillis(week * 7)
//        val interval: Long = if (BuildConfig.DEBUG) TimeUnit.MINUTES.toMillis(days) else TimeUnit.DAYS.toMillis(days)
        val isPersistent = true // persist through boot
        val networkType = JobInfo.NETWORK_TYPE_ANY // Requires some sort of connectivity

        val jobInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            JobInfo.Builder(id, name)
//                .setMinimumLatency(interval)
                .setRequiredNetworkType(networkType)
                .setPeriodic(interval)
                .setPersisted(isPersistent)
                .build()
        } else {
            JobInfo.Builder(id, name)
                .setPeriodic(interval)
                .setRequiredNetworkType(networkType)
                .setPersisted(isPersistent)
                .build()
        }

        return jobInfo
    }
}