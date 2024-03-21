package io.github.kirasok.alarmix.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.repository.AlarmScheduler
import io.github.kirasok.alarmix.presentation.AlarmReceiver

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

  private val alarmManager = context.getSystemService(AlarmManager::class.java)
  override fun schedule(alarm: Alarm) {
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      alarm.id, // request code
      Intent(context, AlarmReceiver::class.java),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmClockInfo =
      AlarmManager.AlarmClockInfo(
        alarm.timestamp.toEpochSecond() * 1000,
        pendingIntent
      ) // accepts in milliseconds
    // Inserts or updates alarm with same pending intent request code
    alarmManager.setAlarmClock(
      alarmClockInfo,
      pendingIntent
    )
  }

  override fun cancel(alarm: Alarm) {
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      alarm.id, // request code
      Intent(context, AlarmReceiver::class.java),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
  }

  override fun canSchedule(): Boolean = alarmManager.canScheduleExactAlarms()
}