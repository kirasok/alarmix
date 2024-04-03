package io.github.kirasok.alarmix.domain.use_case

import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.model.InvalidAlarmError
import io.github.kirasok.alarmix.domain.model.InvalidAlarmException
import io.github.kirasok.alarmix.domain.repository.AlarmRepository
import io.github.kirasok.alarmix.domain.repository.AlarmScheduler
import java.time.ZonedDateTime

data class AlarmUseCases(
  val getAlarms: GetAlarms,
  val getAlarmById: GetAlarmById,
  val insertAlarm: InsertAlarm,
  val deleteAlarm: DeleteAlarm,
  // We don't need ValidateAlarm there because it's used in insertAlarm, not in presentation layer
)

class GetAlarms(private val repository: AlarmRepository) {
  suspend operator fun invoke(): List<Alarm> = repository.getAlarms()
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
    val id =
      repository.insertAlarm(alarm) // alarm.id is set during insertion in DB, repository return id so we can use it in scheduler
    scheduler.schedule(alarm.copy(id = id))
  }
}

class DeleteAlarm(private val repository: AlarmRepository, private val scheduler: AlarmScheduler) {
  suspend operator fun invoke(
    alarm: Alarm,
  ) {
    scheduler.cancel(alarm)
    repository.deleteAlarm(alarm)
  }
}

class ValidateAlarm(private val scheduler: AlarmScheduler) {
  operator fun invoke(
    alarm: Alarm,
  ): Boolean = when {
    alarm.timestamp.isBefore(ZonedDateTime.now())
    -> throw InvalidAlarmException(InvalidAlarmError.PAST_TIMESTAMP)

    !scheduler.canSchedule() -> throw SecurityException(
      "Can't schedule alarm without permission from user"
    )

    else -> true
  }

}