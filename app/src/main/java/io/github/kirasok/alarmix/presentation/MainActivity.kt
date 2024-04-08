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
import io.github.kirasok.alarmix.presentation.dismiss.DismissScreen
import io.github.kirasok.alarmix.presentation.editor.EditorScreen
import io.github.kirasok.alarmix.presentation.permissions.PermissionScreen
import io.github.kirasok.alarmix.presentation.util.Screen
import io.github.kirasok.alarmix.ui.theme.AlarmixTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalPermissionsApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val action = intent.getStringExtra(BUNDLE_ACTION_KEY)?.let {
      BundleAction.valueOf(it)
    }
    val id = intent.getIntExtra(BUNDLE_ALARM_ID_KEY, -1)

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

      val startDestination = when (action) {
        BundleAction.OPEN_EDITOR -> Screen.EditorScreen.route
        BundleAction.OPEN_DISMISS_SCREEN -> Screen.DismissScreen.route
        null -> if (multiplePermissionsState.allPermissionsGranted) Screen.EditorScreen.route else Screen.PermissionsScreen.route // TODO: open alarms list
      }

      AlarmixTheme(darkTheme = true) {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
        ) {
          val navController = rememberNavController()
          NavHost(
            navController = navController,
            startDestination = startDestination
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
                defaultValue = id
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
            composable(
              route = Screen.DismissScreen.route + "?alarmId={alarmId}",
              arguments = listOf(navArgument("alarmId") {
                type = NavType.IntType
                defaultValue = id
              })
            ) {
              DismissScreen(navController = navController)
            }
          }
        }
      }
    }
  }
}

const val BUNDLE_ACTION_KEY = "io.github.kirasok.bundle_action_key"
const val BUNDLE_ALARM_ID_KEY = "io.github.kirasok.bundle_alarm_id_key"

enum class BundleAction {
  OPEN_EDITOR, OPEN_DISMISS_SCREEN
}