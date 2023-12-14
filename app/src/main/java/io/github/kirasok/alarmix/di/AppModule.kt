package io.github.kirasok.alarmix.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.kirasok.alarmix.domain.repository.AlarmRepository
import io.github.kirasok.alarmix.data.repository.AlarmRepositoryLocal
import io.github.kirasok.alarmix.data.source.AlarmDatabase
import io.github.kirasok.alarmix.domain.use_case.AlarmUseCases
import io.github.kirasok.alarmix.domain.use_case.GetAlarms
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  @Provides
  @Singleton
  fun providesNoteDatabase(app: Application): AlarmDatabase = Room.databaseBuilder(
    app,
    AlarmDatabase::class.java,
    AlarmDatabase.DATABASE_NAME
  ).build()

  @Provides
  @Singleton
  fun providesNoteRepository(db: AlarmDatabase): AlarmRepository = AlarmRepositoryLocal(db.alarmDao)

  @Provides
  @Singleton
  fun providesAlarmUseCases(repository: AlarmRepository): AlarmUseCases = AlarmUseCases(
    GetAlarms(repository),
  )
}
