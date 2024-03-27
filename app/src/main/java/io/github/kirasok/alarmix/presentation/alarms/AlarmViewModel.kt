package io.github.kirasok.alarmix.presentation.alarms

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kirasok.alarmix.domain.use_case.AlarmUseCases
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val useCases: AlarmUseCases) : ViewModel() {}