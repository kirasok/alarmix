package io.github.kirasok.alarmix.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import io.github.kirasok.alarmix.domain.use_case.AlarmUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

  @Inject
  lateinit var alarmUseCases: AlarmUseCases
  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
    // Get alarms
    val alarms = runBlocking(Dispatchers.IO) { alarmUseCases.getAlarms() }

    // Enable enabled alarms which timestamp is after now
    alarms.forEach {
      if (it.enabled && it.timestamp.isAfter(ZonedDateTime.now())) {
        runBlocking(Dispatchers.IO) { alarmUseCases.insertAlarm(it) } // On insert, alarm will be validated, replaced in DB and scheduled
      } else if (it.enabled) { // Disable alarms which timestamp is in past
        runBlocking(Dispatchers.IO) { alarmUseCases.insertAlarm(it.copy(enabled = false)) }
      }
    }
  }
}