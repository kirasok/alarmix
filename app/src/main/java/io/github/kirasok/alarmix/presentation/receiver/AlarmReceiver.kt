package io.github.kirasok.alarmix.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class AlarmReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val id = intent.getIntExtra(INTENT_EXTRA_ALARM_ID, -1)
    val serviceIntent =
      Intent(context, AlarmService::class.java).putExtra(INTENT_EXTRA_ALARM_ID, id)
    // We need to use foreground service because we can't start activity from broadcast receiver
    // Instead, the best practice is to show user notification with foreground service
    context.startForegroundService(serviceIntent)
  }

  companion object {
    const val INTENT_EXTRA_ALARM_ID = "intent_extra_alarm_id" // value for passing alarm id within intent
  }
}
