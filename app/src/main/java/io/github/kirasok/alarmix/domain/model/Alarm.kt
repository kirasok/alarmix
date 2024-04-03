package io.github.kirasok.alarmix.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity
data class Alarm(
  @PrimaryKey(autoGenerate = true) val id: Int = 0, // id is generated on **db.insert**, not on creation of object
  val timestamp: ZonedDateTime,
)

data class InvalidAlarmException(val invalidAlarmError: InvalidAlarmError) :
  Exception(invalidAlarmError.toString())

enum class InvalidAlarmError {
  PAST_TIMESTAMP
}