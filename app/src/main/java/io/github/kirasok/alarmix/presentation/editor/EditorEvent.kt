package io.github.kirasok.alarmix.presentation.editor

sealed class EditorEvent {
  data class SetAlarm(val hour: Long, val minute: Long) : EditorEvent()
}