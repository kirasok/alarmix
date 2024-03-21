package io.github.kirasok.alarmix.domain.use_case

import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.model.InvalidAlarmException
import io.github.kirasok.alarmix.domain.repository.AlarmRepository
import io.github.kirasok.alarmix.domain.repository.AlarmScheduler
import kotlinx.coroutines.flow.Flow

data class AlarmUseCases(
  val getAlarms: GetAlarms,
  val getAlarmById: GetAlarmById,
  val insertAlarm: InsertAlarm,
  val deleteAlarm: DeleteAlarm,
  // We don't need ValidateAlarm because it's used in insertAlarm, not in presentation layer
)

class GetAlarms(private val repository: AlarmRepository) {
  operator fun invoke(): Flow<List<Alarm>> = repository.getAlarms()
}

class GetAlarmById(private val repository: AlarmRepository) {
  suspend operator fun invoke(id: Int): Alarm? = repository.getAlarmById(id)
}

class InsertAlarm(
  private val repository: AlarmRepository,
  private val validate: ValidateAlarm,
  private val scheduler: AlarmScheduler,
) {
  suspend operator fun invoke(
    alarm: Alarm,
  ) {
    validate(alarm)
    scheduler.schedule(alarm)
    repository.insertAlarm(alarm)
  }
}

class DeleteAlarm(private val repository: AlarmRepository) {
  suspend operator fun invoke(
    alarm: Alarm,
  ) {
    repository.deleteAlarm(alarm)
  }
}

class ValidateAlarm(private val scheduler: AlarmScheduler) {
  suspend operator fun invoke(
    alarm: Alarm,
  ): Boolean =
    when {
      alarm.timestamp.toEpochSecond() * 1000 < System.currentTimeMillis() // accepts in milliseconds
      -> throw InvalidAlarmException("timestamp can't be less than current time")

      !scheduler.canSchedule() -> throw SecurityException(
        "Can't schedule alarm without permission from user"
      )

      else -> true
    }

}