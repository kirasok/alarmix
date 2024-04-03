package io.github.kirasok.alarmix.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.kirasok.alarmix.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
  @Query("SELECT * FROM alarm")
  fun getAlarms(): Flow<List<Alarm>>

  @Query("SELECT * FROM alarm WHERE id = :id")
  suspend fun getAlarmById(id: Int): Alarm

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAlarm(alarm: Alarm) : Long

  @Delete
  suspend fun deleteAlarm(alarm: Alarm)
}