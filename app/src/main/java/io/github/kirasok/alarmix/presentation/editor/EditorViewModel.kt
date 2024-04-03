package io.github.kirasok.alarmix.presentation.editor

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.model.InvalidAlarmException
import io.github.kirasok.alarmix.domain.use_case.AlarmUseCases
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
  private val useCases: AlarmUseCases,
  savedStateHandle: SavedStateHandle,
) : ViewModel() {
  private val time = mutableStateOf<ZonedDateTime?>(null)

  private val _initialHour = mutableIntStateOf(7)
  val initialHour: State<Int> = _initialHour
  private val _initialMinute = mutableIntStateOf(30)
  val initialMinute: State<Int> = _initialMinute

  private val _eventFlow = MutableSharedFlow<UiEvent>()
  val eventFlow = _eventFlow.asSharedFlow()

  private var currentAlarmId: Int? = null

  init {
    savedStateHandle.get<Int>("alarmId")?.let { alarmId ->
      if (alarmId != -1) viewModelScope.launch {
        useCases.getAlarmById(alarmId)?.also { alarm ->
          currentAlarmId = alarm.id
          time.value = alarm.timestamp
          val diff = Duration.between(time.value, ZonedDateTime.now())
          _initialHour.intValue = diff.toHoursPart()
          _initialMinute.intValue = diff.toMinutesPart()
        }
      }
    }
  }

  fun onEvent(event: EditorEvent) {
    when (event) {
      is EditorEvent.SetAlarm -> viewModelScope.launch {
        try {
          useCases.insertAlarm(
            Alarm(
              timestamp = time.value.run {
                // If we'll initialize time during construction of view model then we can get an InvalidAlarmException because the time could be past current time even with added hours and minutes
                // But checking it for being null there we can use this class to both create new alarm and edit old ones
                this ?: ZonedDateTime.now()
              }.plusHours(event.hour).plusMinutes(event.minute)
            )
          )
          _eventFlow.emit(UiEvent.ShowSnackbar("Successfully set alarm")) // TODO: use stringResources for the string, watch https://www.youtube.com/watch?v=MiLN2vs2Oe0 for explanation of how to set string without sending context into view model
        } catch (e: InvalidAlarmException) {
          _eventFlow.emit(
            UiEvent.ShowSnackbar(
              e.message ?: "Couldn't set alarm" // TODO: and there too
            )
          )
        }
      }
    }
  }

  sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
  }
}