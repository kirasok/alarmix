package io.github.kirasok.alarmix

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AlarmApp : Application() {
  override fun onCreate() {
    super.onCreate()
    createNotificationChannels()
  }

  private fun createNotificationChannels() {
    val channels = mutableListOf<NotificationChannel>()
    // By using enum, we can be sure that we haven't missed any channel
    for (entry in NotificationChannels.entries) {
      when (entry) {
        NotificationChannels.ALARM -> channels.add(
          NotificationChannel(
            NotificationChannels.ALARM.toString(),
            "Alarm notification", // TODO: don't forget to rename on locale change
            NotificationManager.IMPORTANCE_HIGH
          )
        )
      }
    }
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannels(channels)
  }
}