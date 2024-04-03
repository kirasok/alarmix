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
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.kirasok.alarmix.NotificationChannels
import io.github.kirasok.alarmix.R
import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.domain.model.InvalidAlarmError
import io.github.kirasok.alarmix.domain.model.InvalidAlarmException
import io.github.kirasok.alarmix.domain.use_case.AlarmUseCases
import io.github.kirasok.alarmix.presentation.MainActivity
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

  private val context = this // Used within mediaPlayer.apply{} to access context
  private lateinit var vibrator: VibratorManager
  private lateinit var mediaPlayer: MediaPlayer
  private lateinit var alarm: Alarm

  @Inject
  lateinit var useCases: AlarmUseCases

  // Receiver that triggers when user presses button in notification
  private val alarmActionReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      when (intent.getStringExtra(ALARM_ACTION_KEY)?.let { AlarmAction.valueOf(it) }) {
        AlarmAction.DISMISS -> {
          runBlocking { useCases.deleteAlarm(alarm) }
          onDestroy()
        }

        AlarmAction.SNOOZE -> {
          runBlocking { useCases.insertAlarm(alarm.copy(timestamp = alarm.timestamp.plusMinutes(5))) }
          onDestroy()
        }

        null -> throw IllegalStateException("Alarm action can't be null")
      }
    }

  }

  override fun onBind(intent: Intent?): IBinder? = null

  @SuppressLint("UnspecifiedRegisterReceiverFlag") // RECEIVER_EXPORTED requires TIRAMISU but Android Studio still reports warning
  override fun onCreate() {
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
    alarm = runBlocking { useCases.getAlarmById(id) }
      ?: throw InvalidAlarmException(InvalidAlarmError.NULL_ALARM)

    mediaPlayer = MediaPlayer()
    initMediaPlayer()

    vibrator = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

    startForeground(
      if (alarm.id == 0) 1 else alarm.id, // id can't be 0
      createNotification(),
      ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK // required in Android 14
    )

    return START_STICKY
  }

  private fun initMediaPlayer() = mediaPlayer.apply {
    setOnPreparedListener(this@AlarmService)
    setOnErrorListener(this@AlarmService)
    isLooping = true // repeats sound infinitely
    setDataSource(
      context,
      RingtoneManager.getActualDefaultRingtoneUri(
        context,
        RingtoneManager.TYPE_ALARM
      ) // Only getActualDefaultRingtoneUri doesn't throw SecurityException for not having READ_PRIVILEGED_PHONE_STATE
    )
    setAudioAttributes(AudioAttributes.Builder().apply {
      setUsage(AudioAttributes.USAGE_ALARM)
      setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
    }.build()) // sets attribute that the sound is an alarm
    prepare()
  }

  override fun onDestroy() {
    unregisterReceiver(alarmActionReceiver)
    mediaPlayer.stop()
    mediaPlayer.release()
    vibrator.cancel()
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(alarm.id)
    ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    super.onDestroy()
  }

  override fun onPrepared(mp: MediaPlayer?) {
    mp?.start()

    // Handles vibrations
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

  override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
    mp?.apply {
      stop()
      release()
      reset()
      initMediaPlayer()
    }
    return true
  }

  private fun createNotification(): Notification {
    // Starts activity on click on notification
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
      foregroundServiceBehavior = FOREGROUND_SERVICE_IMMEDIATE // Shows notification in foreground
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