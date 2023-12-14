package io.github.kirasok.alarmix.domain.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Alarm(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val timestamp: Long,
  val enabled: Boolean,
  val label: String = "",
  @Embedded val days: Days = Days(),
)

@Entity
data class Days(
  val monday: Boolean = false,
  val tuesday: Boolean = false,
  val wednesday: Boolean = false,
  val thursday: Boolean = false,
  val friday: Boolean = false,
  val saturday: Boolean = false,
  val sunday: Boolean = false,
)

class InvalidAlarmException(message: String) : Exception(message)