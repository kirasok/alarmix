package io.github.kirasok.alarmix.data.repository

import io.github.kirasok.alarmix.data.source.AlarmDao
import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow

class AlarmRepositoryLocal(private val dao: AlarmDao) : AlarmRepository {
  override fun getAlarms(): Flow<List<Alarm>> = dao.getAlarms()

  override suspend fun getAlarmById(id: Int): Alarm = dao.getAlarmById(id)

  override suspend fun insertAlarm(alarm: Alarm) = dao.insertAlarm(alarm)

  override suspend fun deleteAlarm(alarm: Alarm) = dao.deleteAlarm(alarm)
}