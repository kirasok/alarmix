package io.github.kirasok.alarmix.presentation.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(navController: NavController, viewModel: EditorViewModel = hiltViewModel()) {
  Scaffold {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(it),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      val initialHour = 7
      val initialMinute = 30
      val state = rememberTimePickerState(initialHour, initialMinute)
      TimePicker(
        state = state
      )
      ElevatedButton(onClick = {
        viewModel.onEvent(EditorEvent.SetAlarm(state.hour, state.minute))
      }) {
        Text(text = "SET ALARM")
      }
    }
  }
}