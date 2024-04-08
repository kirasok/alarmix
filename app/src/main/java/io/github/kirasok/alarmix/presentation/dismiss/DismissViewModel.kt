package io.github.kirasok.alarmix.presentation.dismiss

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.model.InvalidAlarmError
import io.github.kirasok.alarmix.domain.model.InvalidAlarmException
import io.github.kirasok.alarmix.domain.use_case.AlarmUseCases
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DismissViewModel @Inject constructor(
  private val useCases: AlarmUseCases,
  savedStateHandle: SavedStateHandle,
) : ViewModel() {

  private lateinit var _alarm: MutableState<Alarm>
  val alarm: State<Alarm> = _alarm

  private val _eventFlow = MutableSharedFlow<UiEvent>()
  val eventFlow = _eventFlow.asSharedFlow()

  init {
    savedStateHandle.get<Int>("alarmId")?.let { alarmId ->
      viewModelScope.launch {
        _alarm.value = useCases.getAlarmById(alarmId)
          ?: throw InvalidAlarmException(InvalidAlarmError.NULL_ALARM)
      }
    }
  }

  fun onEvent(event: DismissEvent) = when (event) {
    DismissEvent.DismissAlarm -> viewModelScope.launch {
      useCases.cancelAlarm(alarm.value)
      _eventFlow.emit(UiEvent.CloseScreen)
    }

    DismissEvent.SnoozeAlarm -> viewModelScope.launch {
      useCases.snoozeAlarm(alarm.value)
      _eventFlow.emit(UiEvent.CloseScreen)
    }
  }

  sealed class UiEvent {
    data object CloseScreen : UiEvent()
  }
}