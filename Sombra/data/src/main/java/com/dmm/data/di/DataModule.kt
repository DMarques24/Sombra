package com.dmm.data.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.dmm.data.repository.InMemorySunscreenRepository
import com.dmm.data.repository.OpenMeteoWeatherRepository
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import com.dmm.domain.weather.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds @Singleton
    abstract fun bindSunscreenRepository(impl: InMemorySunscreenRepository): SunscreenRepository

    @Binds @Singleton
    abstract fun bindWeatherRepository(impl: OpenMeteoWeatherRepository): WeatherRepository

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        @Provides @Singleton
        fun provideClock(): Clock = Clock.systemDefaultZone()
    }
}
