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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import io.github.kirasok.alarmix.ui.theme.AlarmixTheme
import java.time.ZonedDateTime

private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    createNotificationChannel(applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
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
  val showSnackbar = rememberSaveable {
    mutableStateOf(false)
  }
  val context = LocalContext.current
  val alarmManager = context.getSystemService(AlarmManager::class.java)
  Column {
    Text(text = time.value)
    ElevatedButton(onClick = {
      val now = ZonedDateTime.now()
      val to = now.plusMinutes(1)
      val alarm = io.github.kirasok.alarmix.domain.model.Alarm(
        timestamp = to.toEpochMillisecond(),
        enabled = true
      )
      time.value = to.toString()

      val pendingIntent = PendingIntent.getBroadcast(
        context,
        alarm.id,
        Intent(context.applicationContext, AlarmReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )
      val openAppIntent = PendingIntent.getActivity(
        context.applicationContext,
        alarm.id,
        Intent(context.applicationContext, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      val alarmClockInfo = AlarmManager.AlarmClockInfo(alarm.timestamp, openAppIntent)
      if (alarmManager.canScheduleExactAlarms()) {
        alarmManager.setAlarmClock(
          alarmClockInfo,
          pendingIntent
        )
      } else {
        showSnackbar.value = true
      }

    }) {
      Text(text = "set time")
    }
    if (showSnackbar.value) {
      Snackbar {
        Text(text = "Couldn't set alarm clock")
      }
    }
  }
}

private fun ZonedDateTime.toEpochMillisecond(): Long = this.toEpochSecond() * 1000

private fun createNotificationChannel(notificationManager: NotificationManager) {
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