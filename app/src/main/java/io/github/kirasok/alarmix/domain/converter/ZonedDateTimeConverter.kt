package io.github.kirasok.alarmix.domain.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTimeConverter {

  @TypeConverter
  fun fromLong(epochSecond: Long): ZonedDateTime =
    ZonedDateTime.ofInstant(
      Instant.ofEpochSecond(epochSecond),
      ZoneId.systemDefault()
    )

  @TypeConverter
  fun toEpochSecond(dateTime: ZonedDateTime): Long = dateTime.toEpochSecond()
}