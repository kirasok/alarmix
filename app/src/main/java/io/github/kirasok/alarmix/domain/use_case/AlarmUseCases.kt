package io.github.kirasok.alarmix.domain.use_case

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.model.InvalidAlarmException
import io.github.kirasok.alarmix.domain.repository.AlarmRepository
import io.github.kirasok.alarmix.presentation.AlarmReceiver
import kotlinx.coroutines.flow.Flow

data class AlarmUseCases(
  val getAlarms: GetAlarms,
  val getAlarmById: GetAlarmById,
  val insertAlarm: InsertAlarm,
  val deleteAlarm: DeleteAlarm,
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
    if (alarm.timestamp.toEpochSecond() * 1000 < System.currentTimeMillis()) // accepts in milliseconds
      throw InvalidAlarmException("timestamp can't be less than current time")
    if (alarmManager.canScheduleExactAlarms()) {
      val alarmClockInfo =
        AlarmManager.AlarmClockInfo(alarm.timestamp.toEpochSecond() * 1000, pendingIntent) // accepts in milliseconds
      // Inserts or updates alarm with same pending intent request code
      alarmManager.setAlarmClock(
        alarmClockInfo,
        pendingIntent
      )
    } else {
      TODO("ask permission from user to set alarms")
    }
    repository.insertAlarm(alarm)
  }
}

class DeleteAlarm(private val repository: AlarmRepository) {
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
    alarmManager.cancel(pendingIntent)
    repository.deleteAlarm(alarm)
  }
}