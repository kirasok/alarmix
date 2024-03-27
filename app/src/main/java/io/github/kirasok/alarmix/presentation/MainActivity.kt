package io.github.kirasok.alarmix.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import io.github.kirasok.alarmix.R
import io.github.kirasok.alarmix.presentation.alarms.AlarmsScreen
import io.github.kirasok.alarmix.presentation.editor.EditorScreen
import io.github.kirasok.alarmix.presentation.permissions.PermissionScreen
import io.github.kirasok.alarmix.presentation.util.Screen
import io.github.kirasok.alarmix.ui.theme.AlarmixTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalPermissionsApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {

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

      AlarmixTheme(darkTheme = true) {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
        ) {
          val navController = rememberNavController()
          NavHost(
            navController = navController,
            startDestination = if (multiplePermissionsState.allPermissionsGranted) Screen.EditorScreen.route else Screen.PermissionsScreen.route
          ) {
            composable(route = Screen.AlarmsScreen.route) {
              AlarmsScreen(navController = navController)
            }
            composable(
              route = Screen.EditorScreen.route + "?alarmId={alarmId}",
              arguments = listOf(navArgument(
                name = "alarmId",
              ) {
                type = NavType.IntType
                defaultValue = -1
              }),
            ) {
              EditorScreen(navController = navController)
            }
            composable(
              route = Screen.PermissionsScreen.route
            ) {
              PermissionScreen(
                navController = navController,
                multiplePermissionsState = multiplePermissionsState,
                rationale = rationale
              )
            }
          }
        }
      }
    }
  }
}