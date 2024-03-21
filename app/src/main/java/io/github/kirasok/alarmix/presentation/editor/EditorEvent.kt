package io.github.kirasok.alarmix.presentation.editor

import io.github.kirasok.alarmix.domain.model.Alarm

sealed class EditorEvent {
  data class SetAlarm(val hour: Int, val minute: Int) : EditorEvent()
}