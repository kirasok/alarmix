package io.github.kirasok.alarmix.presentation.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import io.github.kirasok.alarmix.NotificationChannels
import io.github.kirasok.alarmix.R


class AlarmReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val manager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val builder =
      NotificationCompat.Builder(context, NotificationChannels.ALARM.toString())
        .setSmallIcon(R.drawable.alarm)
        .setContentTitle("Alarmix!")
        .setContentTitle("Wake up!")
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
    manager.notify(intent.getIntExtra(INTENT_EXTRA_ALARM_ID, 0), builder.build())
  }

  companion object {
    const val INTENT_EXTRA_ALARM_ID = "intent_extra_alarm_id"
  }
}
