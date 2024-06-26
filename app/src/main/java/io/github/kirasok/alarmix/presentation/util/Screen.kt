package io.github.kirasok.alarmix.presentation.util

sealed class Screen(val route: String) {
  data object AlarmsScreen : Screen("alarms_screen")
  data object EditorScreen : Screen("editor_screen")
  data object PermissionsScreen : Screen("permissions_screen")

  data object DismissScreen : Screen("dismiss_screen")
}