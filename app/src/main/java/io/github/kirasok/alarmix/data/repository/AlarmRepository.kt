package io.github.kirasok.alarmix.data.repository

import io.github.kirasok.alarmix.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
  fun getAlarms(): Flow<List<Alarm>>

  suspend fun getAlarmById(id: Int): Alarm?

  suspend fun insertAlarm(alarm: Alarm)

  suspend fun deleteAlarm(alarm: Alarm)
}
