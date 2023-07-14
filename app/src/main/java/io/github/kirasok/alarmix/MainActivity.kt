package io.github.kirasok.alarmix

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import io.github.kirasok.alarmix.ui.theme.AlarmixTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


private const val NOTIFICATION_ID = 1
private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificaionChannel(applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        setContent {
            AlarmixTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Alarm()
                }
            }
        }
    }
}

@Composable
fun Alarm() {
    val time = rememberSaveable {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    Column {
        Text(text = time.value.toString())
        ElevatedButton(onClick = {
            val now = LocalDateTime.now()
            val to = now.plusMinutes(4)
            time.value = to.toString()

            val intent = Intent(context, AlarmReceiver::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0, intent, PendingIntent.FLAG_IMMUTABLE
            )

            val zoneinfo = ZoneId
                .systemDefault()
                .getRules()
                .getOffset(
                    Instant.now()
                )
            alarmManager!!.setAlarmClock(
                AlarmManager.AlarmClockInfo(
                    to.toEpochSecond(zoneinfo) * 1000,
                    pendingIntent
                ), pendingIntent
            )
        }) {
            Text(text = "set time")
        }
    }
}

private fun createNotificaionChannel(notificationManager: NotificationManager) {
    // Create the NotificationChannel with all the parameters.
    val notificationChannel = NotificationChannel(
        PRIMARY_CHANNEL_ID,
        "Stand Up Notification",
        NotificationManager.IMPORTANCE_DEFAULT
    )
    notificationChannel.enableLights(true)
    notificationChannel.lightColor = Color.Red.value.toInt()
    notificationChannel.enableVibration(true)
    notificationChannel.description = "Notifies every 15 minutes to stand up and walk"
    notificationManager.createNotificationChannel(notificationChannel)
}