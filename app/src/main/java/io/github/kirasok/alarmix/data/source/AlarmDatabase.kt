package io.github.kirasok.alarmix.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.kirasok.alarmix.domain.converter.ZonedDateTimeConverter
import io.github.kirasok.alarmix.domain.model.Alarm

@Database(
  entities = [Alarm::class],
  version = 1,
  exportSchema = false
)
@TypeConverters(ZonedDateTimeConverter::class)
abstract class AlarmDatabase : RoomDatabase() {
  abstract val alarmDao: AlarmDao

  companion object {
    const val DATABASE_NAME = "alarm_db"
  }
}