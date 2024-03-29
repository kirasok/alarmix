package io.github.kirasok.alarmix.presentation.receiver

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.CombinedVibration
import android.os.IBinder
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.kirasok.alarmix.NotificationChannels
import io.github.kirasok.alarmix.R
import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.use_case.AlarmUseCases
import io.github.kirasok.alarmix.presentation.MainActivity
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service() {

  private var isPlaying = false
  private lateinit var vibrator: VibratorManager
  private var mediaPlayer: MediaPlayer? = null // TODO: fix audio
  private lateinit var alarm: Alarm

  @Inject
  lateinit var useCases: AlarmUseCases

  private val alarmActionReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      when (intent.getStringExtra(ALARM_ACTION_KEY)?.let { AlarmAction.valueOf(it) }) {
        AlarmAction.DISMISS -> onDestroy()
        AlarmAction.SNOOZE -> onDestroy() // TODO: implement snooze
        null -> throw IllegalStateException("Alarm action can't be null")
      }
    }

  }

  override fun onBind(intent: Intent?): IBinder? = null

  @SuppressLint("UnspecifiedRegisterReceiverFlag") // RECEIVER_EXPORTED requires TIRAMISU but Android Studio still reports warning
  override fun onCreate() {
    vibrator = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

    val intentFilter = IntentFilter(ALARM_ACTION_KEY)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) registerReceiver(
      alarmActionReceiver, intentFilter, RECEIVER_EXPORTED
    )
    else registerReceiver(
      alarmActionReceiver, intentFilter
    )


    super.onCreate()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val id = intent?.getIntExtra(AlarmReceiver.INTENT_EXTRA_ALARM_ID, -1).takeIf { it != -1 }
      ?: throw IllegalStateException("Alarm id can't be null")
    // val alarm = runBlocking { useCases.getAlarmById(id) } TODO: fix getting alarm
    //   ?: throw InvalidAlarmException("Alarm can't be null")
    this.alarm = Alarm(id, ZonedDateTime.now())
    Log.d(null, "Got $alarm; timestamp: ${alarm.timestamp}; id: ${alarm.id}")
    startForeground(
      if (alarm.id == 0) 1 else alarm.id, // id can't be 0
      createNotification(),
      ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK // required in Android 14
    )
    return START_STICKY
  }

  override fun onDestroy() {
    unregisterReceiver(alarmActionReceiver)
    ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    super.onDestroy()
  }

  private fun playMedia() {
    stopMedia() // If we currently playing

    // Play sound
    val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    mediaPlayer = MediaPlayer()
    mediaPlayer!!.setOnErrorListener { player, _, _ ->
      player.stop()
      player.release()
      mediaPlayer = null
      true
    }
    try {
      mediaPlayer!!.setDataSource(this, sound)
      playAlarm(mediaPlayer!!)
    } catch (e: Exception) {
      Log.e(null, "Failed to play sound on alarm: ${e.message}")
    }

    val combinedVibration = CombinedVibration.createParallel(
      VibrationEffect.createWaveform(
        longArrayOf(
          0, 5000
        ), 0
      )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) vibrator.vibrate(
      combinedVibration, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM)
    )
    else vibrator.vibrate(combinedVibration)
  }

  private fun playAlarm(player: MediaPlayer) {
    player.isLooping = true
    val audioAttributes = AudioAttributes.Builder().apply {
      setUsage(AudioAttributes.USAGE_ALARM)
      setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
    }.build()
    player.setAudioAttributes(audioAttributes)
    player.prepare()
    player.start()
  }

  private fun stopMedia() {
    if (!isPlaying) return
    isPlaying = false
    if (mediaPlayer != null) {
      mediaPlayer?.stop()
      mediaPlayer?.release()
      mediaPlayer = null
    }

    vibrator.cancel()
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(alarm.id)
  }

  private fun createNotification(): Notification {
    val pendingIntent = PendingIntent.getActivity(
      this, alarm.id, Intent(this, MainActivity::class.java).apply {
        addFlags(
          Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        )
      }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val dismissIntent =
      Intent(ALARM_ACTION_KEY).putExtra(ALARM_ACTION_KEY, AlarmAction.DISMISS.toString())
    val dismissAction =
      NotificationCompat.Action.Builder(null, "Dismiss", getPendingIntent(dismissIntent, alarm.id))
        .build()

    val snoozeIntent =
      Intent(ALARM_ACTION_KEY).putExtra(ALARM_ACTION_KEY, AlarmAction.SNOOZE.toString())
    val snoozeAction =
      NotificationCompat.Action.Builder(null, "Snooze", getPendingIntent(snoozeIntent, alarm.id))
        .build()

    return NotificationCompat.Builder(
      this, NotificationChannels.ALARM.toString()
    ).apply {
      setSmallIcon(R.drawable.alarm)
      setContentTitle("Alarmix!")
      setContentText("Wake up!")
      setOngoing(true) // User can't dismiss notification
      setPriority(NotificationCompat.PRIORITY_MAX)
      setCategory(NotificationCompat.CATEGORY_ALARM)
      foregroundServiceBehavior = FOREGROUND_SERVICE_IMMEDIATE
      setFullScreenIntent(pendingIntent, true)
      addAction(dismissAction)
      addAction(snoozeAction)
    }.build()
  }

  private fun getPendingIntent(intent: Intent, requestCode: Int): PendingIntent =
    PendingIntent.getBroadcast(
      this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

  companion object {

    const val ALARM_ACTION_KEY = "alarm_action_key"

    enum class AlarmAction {
      DISMISS, SNOOZE
    }
  }
}