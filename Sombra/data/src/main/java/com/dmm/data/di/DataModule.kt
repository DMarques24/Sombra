package com.dmm.data.di

import com.dmm.data.reminder.AlarmReminderScheduler
import com.dmm.data.repository.FusedLocationRepository
import com.dmm.data.repository.OpenMeteoWeatherRepository
import com.dmm.data.repository.PreferencesSunscreenRepository
import com.dmm.data.repository.PreferencesUserRepository
import com.dmm.data.repository.PreferencesUvHistoryRepository
import com.dmm.data.repository.SupabaseAuthRepository
import com.dmm.domain.auth.repository.AuthRepository
import com.dmm.domain.location.repository.LocationRepository
import com.dmm.domain.profile.repository.UserRepository
import com.dmm.domain.progress.repository.UvHistoryRepository
import com.dmm.domain.sunscreen.repository.ReminderScheduler
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import com.dmm.domain.weather.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds @Singleton
    abstract fun bindSunscreenRepository(impl: PreferencesSunscreenRepository): SunscreenRepository

    @Binds @Singleton
    abstract fun bindWeatherRepository(impl: OpenMeteoWeatherRepository): WeatherRepository

    @Binds @Singleton
    abstract fun bindLocationRepository(impl: FusedLocationRepository): LocationRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: PreferencesUserRepository): UserRepository

    @Binds @Singleton
    abstract fun bindUvHistoryRepository(impl: PreferencesUvHistoryRepository): UvHistoryRepository

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: SupabaseAuthRepository): AuthRepository

    @Binds @Singleton
    abstract fun bindReminderScheduler(impl: AlarmReminderScheduler): ReminderScheduler

    companion object {
        @Provides @Singleton
        fun provideClock(): Clock = Clock.systemDefaultZone()

        @Provides @Singleton @ApplicationScope
        fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
