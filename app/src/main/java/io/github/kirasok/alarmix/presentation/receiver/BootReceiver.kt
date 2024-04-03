package io.github.kirasok.alarmix.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.use_case.AlarmUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class BootReceiver : BroadcastReceiver() {

  @Inject
  lateinit var alarmUseCases: AlarmUseCases
  override fun onReceive(context: Context?, intent: Intent?) {
    // Get alarms
    var alarms: List<Alarm> = listOf()
    runBlocking(Dispatchers.IO) {
      alarmUseCases.getAlarms().catch { Log.e(null, it.toString()) }.collect { alarms = it }
    }

    // Enable enabled alarms
    alarms.forEach {
      if (it.enabled) runBlocking(Dispatchers.IO) { alarmUseCases.insertAlarm(it) } // On insert, alarm will be validated, replaced in DB and scheduled
    }
  }
}