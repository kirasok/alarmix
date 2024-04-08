package io.github.kirasok.alarmix.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import io.github.kirasok.alarmix.domain.model.InvalidAlarmError
import io.github.kirasok.alarmix.domain.model.InvalidAlarmException
import io.github.kirasok.alarmix.domain.use_case.AlarmUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

  @Inject
  lateinit var alarmUseCases: AlarmUseCases
  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
    // Get alarms
    val alarms = runBlocking(Dispatchers.IO) { alarmUseCases.getAlarms() }

    // Insert each alarm
    alarms.forEach { alarm ->
      runBlocking {
        try {
          alarmUseCases.scheduleAlarm(alarm) // insertAlarm validates alarm and throws InvalidAlarmException if alarm is not valid
        } catch (e: InvalidAlarmException) {
          when (e.invalidAlarmError) {
            InvalidAlarmError.PAST_TIMESTAMP -> alarmUseCases.cancelAlarm(alarm) // If alarm has past timestamp then we delete it
            else -> {}
          }
        }
      }
    }
  }
}