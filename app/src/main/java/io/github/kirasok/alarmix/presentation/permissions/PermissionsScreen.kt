package io.github.kirasok.alarmix.presentation.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import io.github.kirasok.alarmix.R
import io.github.kirasok.alarmix.presentation.util.Screen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(
  navController: NavController,
  multiplePermissionsState: MultiplePermissionsState,
  rationale: String,
) {
  if (multiplePermissionsState.allPermissionsGranted) navController.navigate(Screen.EditorScreen.route)

  Scaffold {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(it),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = if (multiplePermissionsState.shouldShowRationale) rationale else stringResource(R.string.permissions_demand),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(24.dp)
      )
      Button(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
        Text(text = "Grant permissions")
      }
    }
  }
}
