package io.github.kirasok.alarmix.domain.use_case

import android.app.AlarmManager
import io.github.kirasok.alarmix.data.repository.AlarmRepository
import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.model.InvalidAlarmException
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
  suspend operator fun invoke(alarm: Alarm, alarmManager: AlarmManager) {
    if (alarm.timestamp < System.currentTimeMillis())
      throw InvalidAlarmException("timestamp can't be less than current time")
    if (!alarm.enabled)
      TODO("Check alarmManager if alarm is set and unset it")
//      alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(alarm.timestamp))
    TODO()
  }
}