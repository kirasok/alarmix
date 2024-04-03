package io.github.kirasok.alarmix.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity
data class Alarm(
  @PrimaryKey(autoGenerate = true) val id: Int = 0, // id is generated on **db.insert**, not on creation of object
  val timestamp: ZonedDateTime,
  var enabled: Boolean = true,
)

class InvalidAlarmException(message: String) : Exception(message)