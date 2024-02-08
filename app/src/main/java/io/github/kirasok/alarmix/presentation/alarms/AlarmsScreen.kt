package io.github.kirasok.alarmix.presentation.alarms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun AlarmsScreen(navController: NavController, viewModel: AlarmViewModel = hiltViewModel()) {
  Scaffold {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(it), contentAlignment = Alignment.Center
    ) {
      Text(text = "WIP AlarmsScreen")
    }
  }
}