package io.github.kirasok.alarmix.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import io.github.kirasok.alarmix.presentation.alarms.AlarmsScreen
import io.github.kirasok.alarmix.presentation.editor.EditorScreen
import io.github.kirasok.alarmix.presentation.util.Screen
import io.github.kirasok.alarmix.ui.theme.AlarmixTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AlarmixTheme(darkTheme = true) {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
        ) {
          val navController = rememberNavController()
          NavHost(
            navController = navController,
            startDestination = Screen.EditorScreen.route
          ) {
            composable(route = Screen.AlarmsScreen.route) {
              AlarmsScreen(navController = navController)
            }
            composable(
              route = Screen.EditorScreen.route + "?alarmId={alarmId}",
              arguments = listOf(
                navArgument(
                  name = "alarmId",
                ) {
                  type = NavType.IntType
                  defaultValue = -1
                }
              ),
            ) {
              EditorScreen(navController = navController)
            }
          }
        }
      }
    }
  }
}