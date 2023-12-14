package io.github.kirasok.alarmix.domain.use_case

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.kirasok.alarmix.AlarmReceiver
import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.model.InvalidAlarmException
import io.github.kirasok.alarmix.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow

data class AlarmUseCases(
  val getAlarms: GetAlarms,
)

class GetAlarms(private val repository: AlarmRepository) {
  operator fun invoke(): Flow<List<Alarm>> = repository.getAlarms()
}

class GetAlarmById(private val repository: AlarmRepository) {
  suspend operator fun invoke(id: Int): Alarm? = repository.getAlarmById(id)
}

class InsertAlarm(private val repository: AlarmRepository) {
  suspend operator fun invoke(
    alarm: Alarm,
    context: Context,
  ) {
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
      context.applicationContext,
      alarm.id, // request code
      Intent(context.applicationContext, AlarmReceiver::class.java),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent) // Will cancel pending intent with same request code
    if (alarm.timestamp < System.currentTimeMillis())
      throw InvalidAlarmException("timestamp can't be less than current time")
    if (alarmManager.canScheduleExactAlarms()) {
      if (alarm.enabled) {
        val alarmClockInfo = AlarmManager.AlarmClockInfo(alarm.timestamp, pendingIntent)
        // Inserts or updates alarm with same pending intent request code
        alarmManager.setAlarmClock(
          alarmClockInfo,
          pendingIntent
        )
      }
    } else {
      TODO("ask permission from user to set alarms")
    }
    repository.insertAlarm(alarm)
  }
}