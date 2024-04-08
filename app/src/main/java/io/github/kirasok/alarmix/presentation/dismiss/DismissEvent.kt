package io.github.kirasok.alarmix.presentation.dismiss

sealed class DismissEvent() {
  data object DismissAlarm : DismissEvent()

  data object SnoozeAlarm: DismissEvent()
}
