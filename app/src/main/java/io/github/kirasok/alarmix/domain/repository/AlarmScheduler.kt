package io.github.kirasok.alarmix.domain.repository

import io.github.kirasok.alarmix.domain.model.Alarm

// AlarmScheduler interface allows us to schedule alarms within domain layer by providing context with DI
interface AlarmScheduler {

  fun schedule(alarm: Alarm)

  fun cancel(alarm: Alarm)

  fun canSchedule(): Boolean
}