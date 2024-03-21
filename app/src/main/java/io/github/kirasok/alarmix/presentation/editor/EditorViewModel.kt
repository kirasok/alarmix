package io.github.kirasok.alarmix.presentation.editor

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.use_case.AlarmUseCases
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
  private val useCases: AlarmUseCases,
  savedStateHandle: SavedStateHandle,
) : ViewModel() {
  private val time = mutableStateOf<ZonedDateTime?>(null)

  private var currentAlarmId: Int? = null

  init {
    savedStateHandle.get<Int>("alarmId")?.let { alarmId ->
      if (alarmId != -1)
        viewModelScope.launch {
          useCases.getAlarmById(alarmId)?.also { alarm ->
            currentAlarmId = alarm.id
            time.value = alarm.timestamp
          }
        }
    }
  }

  fun onEvent(event: EditorEvent) {
    when (event) {
      is EditorEvent.SetAlarm -> viewModelScope.launch {
        useCases.insertAlarm(
          Alarm(
            timestamp = time.value.run {
              // If we'll initialize time during construction of view model then we can get an InvalidAlarmException because the time could be past current time even with added hours and minutes
              // But checking it for being null there we can use this class to both create new alarm and edit old ones
              this ?: ZonedDateTime.now()
            }.plusHours(event.hour).plusMinutes(event.minute)
          )
        )
      }
    }
  }
}