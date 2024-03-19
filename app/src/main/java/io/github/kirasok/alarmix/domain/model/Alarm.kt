package io.github.kirasok.alarmix.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity
data class Alarm(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val timestamp: ZonedDateTime,
)

class InvalidAlarmException(message: String) : Exception(message)