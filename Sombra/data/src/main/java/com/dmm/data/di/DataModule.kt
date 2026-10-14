package com.dmm.data.di

import com.dmm.data.repository.FusedLocationRepository
import com.dmm.data.repository.InMemorySunscreenRepository
import com.dmm.data.repository.OpenMeteoWeatherRepository
import com.dmm.domain.location.repository.LocationRepository
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

    @Binds @Singleton
    abstract fun bindLocationRepository(impl: FusedLocationRepository): LocationRepository

    companion object {
        @Provides @Singleton
        fun provideClock(): Clock = Clock.systemDefaultZone()
    }
}
