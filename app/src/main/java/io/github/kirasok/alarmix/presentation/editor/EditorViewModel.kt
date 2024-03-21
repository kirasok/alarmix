package io.github.kirasok.alarmix.presentation.editor

import androidx.compose.runtime.State
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
  private val _time = mutableStateOf(ZonedDateTime.now())
  val time: State<ZonedDateTime> = _time

  private var currentAlarmId: Int? = null

  init {
    savedStateHandle.get<Int>("alarmId")?.let { alarmId ->
      if (alarmId != -1)
        viewModelScope.launch {
          useCases.getAlarmById(alarmId)?.also { alarm ->
            currentAlarmId = alarm.id
            _time.value = alarm.timestamp
          }
        }
    }
  }

  fun onEvent(event: EditorEvent) {
    when (event) {
      is EditorEvent.SetAlarm -> TODO()
    }
  }
}