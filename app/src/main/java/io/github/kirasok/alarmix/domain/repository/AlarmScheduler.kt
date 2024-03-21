package io.github.kirasok.alarmix.domain.repository

import io.github.kirasok.alarmix.domain.model.Alarm

interface AlarmScheduler {

  fun schedule(alarm: Alarm)

  fun cancel(alarm: Alarm)

  fun canSchedule(): Boolean
}