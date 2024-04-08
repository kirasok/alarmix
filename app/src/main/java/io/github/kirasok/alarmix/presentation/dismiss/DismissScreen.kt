package io.github.kirasok.alarmix.presentation.dismiss

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DismissScreen(navController: NavController, viewModel: DismissViewModel = hiltViewModel()) {

  val finishAlarm = remember {
    mutableStateOf(false)
  }

  LaunchedEffect(key1 = true) {
    viewModel.eventFlow.collectLatest { event ->
      when (event) {
        DismissViewModel.UiEvent.CloseScreen -> finishAlarm.value = true
      }
    }
  }

  if (finishAlarm.value) (LocalContext.current as Activity?)?.finish()

  Scaffold {
    Column(modifier = Modifier.padding(it)) {
      Text(
        text = viewModel.alarm.value.timestamp.format(
          DateTimeFormatter.ofLocalizedTime(
            FormatStyle.SHORT
          )
        )
      )

      Row {
        ElevatedButton(onClick = { viewModel.onEvent(DismissEvent.DismissAlarm) }) {
          Text(text = "Dismiss")
        }

        Spacer(modifier = Modifier.width(8.dp))

        ElevatedButton(onClick = { viewModel.onEvent(DismissEvent.SnoozeAlarm) }) {
          Text(text = "Snooze")
        }
      }
    }
  }
}