package io.github.kirasok.alarmix.presentation.editor

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kirasok.alarmix.domain.use_case.AlarmUseCases
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(private val useCases: AlarmUseCases) : ViewModel() {
}