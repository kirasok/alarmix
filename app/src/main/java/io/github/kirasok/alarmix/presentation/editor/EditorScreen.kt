package io.github.kirasok.alarmix.presentation.editor

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.github.kirasok.alarmix.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EditorScreen(navController: NavController, viewModel: EditorViewModel = hiltViewModel()) {
  val snackbarHostState = remember { SnackbarHostState() }

  val permissionList = mutableListOf<String>()
  var rationale = stringResource(R.string.permissions_rationale)
  // POST_NOTIFICATIONS requires user permission from TIRAMISU (33)
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    permissionList.add(Manifest.permission.POST_NOTIFICATIONS)
    rationale += stringResource(R.string.permissions_notification)
  }
  // SCHEDULE_EXACT_ALARM requires user permission from UPSIDE_DOWN_CAKE (34)
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
    permissionList.add(Manifest.permission.SCHEDULE_EXACT_ALARM)
    rationale += stringResource(R.string.permissions_schedule)
  }
  val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissionList)


  LaunchedEffect(key1 = true) {
    viewModel.eventFlow.collectLatest { event ->
      when (event) {
        is EditorViewModel.UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(message = event.message)
      }
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(it),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (!multiplePermissionsState.allPermissionsGranted) {
        Text(
          text = if (multiplePermissionsState.shouldShowRationale) rationale else stringResource(
            R.string.permissions_demand
          ),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(24.dp)
        )
        Button(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
          Text(text = "Grant permissions")
        }
      } else {
        val initialHour = 7
        val initialMinute = 30
        val timePickerState = rememberTimePickerState(initialHour, initialMinute)

        Text(
          text = stringResource(R.string.editor_hint),
          modifier = Modifier.padding(24.dp),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.size(24.dp))
        TimePicker(
          state = timePickerState
        )
        ElevatedButton(onClick = {
          viewModel.onEvent(
            EditorEvent.SetAlarm(
              timePickerState.hour.toLong(),
              timePickerState.minute.toLong()
            )
          )
        }) {
          Text(text = stringResource(R.string.set_alarm))
        }
      }
    }
  }
}
