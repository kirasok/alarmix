package io.github.kirasok.alarmix.domain.repository

import io.github.kirasok.alarmix.domain.model.Alarm

interface AlarmRepository {
  suspend fun getAlarms(): List<Alarm>

  suspend fun getAlarmById(id: Int): Alarm?

  suspend fun insertAlarm(alarm: Alarm): Int

  suspend fun deleteAlarm(alarm: Alarm)
}
