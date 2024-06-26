package io.github.kirasok.alarmix.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.kirasok.alarmix.data.repository.AlarmRepositoryLocal
import io.github.kirasok.alarmix.data.repository.AlarmSchedulerImpl
import io.github.kirasok.alarmix.data.source.AlarmDatabase
import io.github.kirasok.alarmix.domain.repository.AlarmRepository
import io.github.kirasok.alarmix.domain.repository.AlarmScheduler
import io.github.kirasok.alarmix.domain.use_case.AlarmUseCases
import io.github.kirasok.alarmix.domain.use_case.CancelAlarm
import io.github.kirasok.alarmix.domain.use_case.GetAlarmById
import io.github.kirasok.alarmix.domain.use_case.GetAlarms
import io.github.kirasok.alarmix.domain.use_case.ScheduleAlarm
import io.github.kirasok.alarmix.domain.use_case.SnoozeAlarm
import io.github.kirasok.alarmix.domain.use_case.ValidateAlarm
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  @Provides
  @Singleton
  fun providesNoteDatabase(app: Application): AlarmDatabase = Room.databaseBuilder(
    app, AlarmDatabase::class.java, AlarmDatabase.DATABASE_NAME
  ).build()

  @Provides
  @Singleton
  fun providesNoteRepository(db: AlarmDatabase): AlarmRepository = AlarmRepositoryLocal(db.alarmDao)

  @Provides
  @Singleton
  fun providesScheduler(app: Application): AlarmScheduler =
    AlarmSchedulerImpl(app.applicationContext)

  @Provides
  fun provideValidateAlarmCase(scheduler: AlarmScheduler) = ValidateAlarm(scheduler)

  @Provides
  @Singleton
  fun providesAlarmUseCases(
    repository: AlarmRepository,
    scheduler: AlarmScheduler,
    validator: ValidateAlarm,
  ): AlarmUseCases {
    val getAlarms = GetAlarms(repository)
    val getAlarmById = GetAlarmById(repository)
    val scheduleAlarm = ScheduleAlarm(repository, validator, scheduler)
    val cancelAlarm = CancelAlarm(repository, scheduler)
    val snoozeAlarm = SnoozeAlarm(scheduleAlarm)
    return AlarmUseCases(
      getAlarms,
      getAlarmById,
      scheduleAlarm,
      cancelAlarm,
      snoozeAlarm
    )
  }
}
