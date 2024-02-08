package io.github.kirasok.alarmix.presentation

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import io.github.kirasok.alarmix.R


class AlarmReceiver : BroadcastReceiver() {
  private lateinit var mNotificationManager: NotificationManager
  override fun onReceive(context: Context, intent: Intent) {
    Log.d(TAG, "onReceive: AlarmReceiver Class")
    mNotificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    deliverNotification(context)
  }

  private fun deliverNotification(context: Context) {
    Log.d(TAG, "deliverNotification: AlarmReceiver Class")
    val contentIntent = Intent(context, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
      context,
      NOTIFICATION_ID,
      contentIntent,
      PendingIntent.FLAG_IMMUTABLE
    )
    val builder: NotificationCompat.Builder =
      NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
        .setSmallIcon(R.drawable.alarm)
        .setContentTitle("Stand Up Alert")
        .setContentText("You should stand up and walk around now!")
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
    mNotificationManager.notify(NOTIFICATION_ID, builder.build())
  }

  companion object {
    private const val NOTIFICATION_ID = 0
    private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    private const val TAG = "Lord"
  }
}
