package io.github.kirasok.alarmix.domain.model

import androidx.room.Entity

@Entity
data class Alarm(
  val timestamp: Int,
  val enabled: Boolean,
  val label: String = "",
  val days: Days = Days(),
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
