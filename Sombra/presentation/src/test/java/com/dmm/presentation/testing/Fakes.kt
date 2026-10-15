package com.dmm.presentation.testing

import com.dmm.domain.auth.model.AuthResult
import com.dmm.domain.auth.model.AuthUser
import com.dmm.domain.auth.model.SignUpOutcome
import com.dmm.domain.auth.repository.AuthRepository
import com.dmm.domain.common.DataError
import com.dmm.domain.common.DomainResult
import com.dmm.domain.location.model.Coordinates
import com.dmm.domain.location.repository.LocationRepository
import com.dmm.domain.profile.model.UserProfile
import com.dmm.domain.profile.repository.UserRepository
import com.dmm.domain.progress.repository.UvHistoryRepository
import com.dmm.domain.sunscreen.model.SunscreenEvent
import com.dmm.domain.sunscreen.model.SunscreenSession
import com.dmm.domain.sunscreen.repository.ReminderScheduler
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import com.dmm.domain.weather.model.CurrentWeather
import com.dmm.domain.weather.model.DailyForecast
import com.dmm.domain.weather.model.Forecast
import com.dmm.domain.weather.model.HourlyUv
import com.dmm.domain.weather.model.WeatherCondition
import com.dmm.domain.weather.repository.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

// Fakes (implementações simples em memória) em vez de mocks: o teste controla o que cada uma devolve

// Relógio que o teste pode avançar
class MutableClock(private var now: Instant, private val zone: ZoneId = ZoneOffset.UTC) : Clock() {
    fun advance(duration: Duration) {
        now = now.plus(duration)
    }
    override fun instant(): Instant = now
    override fun getZone(): ZoneId = zone
    override fun withZone(zone: ZoneId): Clock = MutableClock(now, zone)
}

class FakeWeatherRepository(var result: DomainResult<Forecast>) : WeatherRepository {
    var calls = 0

    // Tempo (virtual) que o pedido demora: com 0 o Loading e o Content acontecem no mesmo instante
    // e o StateFlow só guarda o último, por isso os testes que querem ver o Loading usam um atraso
    var delayMillis = 0L

    override suspend fun getForecast(latitude: Double, longitude: Double): DomainResult<Forecast> {
        calls++
        if (delayMillis > 0) delay(delayMillis)
        return result
    }

    companion object {
        // Um dia de verão em UTC: UV 8 agora (12h) e horas de 9h a 18h com UV a subir e a descer
        fun forecast(date: LocalDate, fetchedAt: Instant, uvNow: Double = 8.0, isFromCache: Boolean = false) = Forecast(
            current = CurrentWeather(temperatureC = 27.4, feelsLikeC = 28.2, isDay = true, condition = WeatherCondition.Clear, uvIndex = uvNow),
            hourly = listOf(9 to 3.0, 10 to 5.0, 11 to 7.0, 12 to uvNow, 13 to 8.0, 14 to 7.0, 15 to 6.0, 16 to 4.0, 17 to 2.0)
                .map { (hour, uv) -> HourlyUv(date.atTime(hour, 0), uv) },
            daily = listOf(DailyForecast(date, WeatherCondition.Clear, 29.0, 18.0, 8.0, LocalTime.of(20, 30))),
            fetchedAt = fetchedAt,
            zone = ZoneOffset.UTC,
            isFromCache = isFromCache,
        )

        fun success(date: LocalDate, fetchedAt: Instant, uvNow: Double = 8.0, isFromCache: Boolean = false) =
            DomainResult.Success(forecast(date, fetchedAt, uvNow, isFromCache))

        fun failure(error: DataError) = DomainResult.Failure(error)
    }
}

class FakeLocationRepository(
    var location: Coordinates? = Coordinates(40.2, -8.4),
    var cityName: String? = "Coimbra",
) : LocationRepository {
    override suspend fun getCurrentLocation() = location
    override suspend fun getCityName(coordinates: Coordinates) = cityName
    override suspend fun getCoordinates(cityName: String): Coordinates? = null
}

class FakeUserRepository(profile: UserProfile = UserProfile()) : UserRepository {
    val profile = MutableStateFlow(profile)
    override fun observeProfile(): Flow<UserProfile> = profile
    override suspend fun update(transform: (UserProfile) -> UserProfile) = profile.update(transform)
    override suspend fun syncFromRemote() = Unit
    override suspend fun clear() {
        profile.value = UserProfile()
    }
}

class FakeSunscreenRepository(private val clock: Clock) : SunscreenRepository {
    val session = MutableStateFlow<SunscreenSession?>(null)
    val history = MutableStateFlow<List<SunscreenEvent>>(emptyList())

    // Tempo (virtual) que guardar demora: deixa o teste tocar duas vezes antes de a primeira acabar
    var delayMillis = 0L

    override fun observeActiveSession(): Flow<SunscreenSession?> = session
    override suspend fun start(spf: Int, inWater: Boolean): SunscreenSession {
        if (delayMillis > 0) delay(delayMillis)
        return SunscreenSession(clock.instant(), spf, inWater).also { session.value = it }
    }
    override suspend fun reapply() = session.value?.copy(appliedAt = clock.instant())?.also { session.value = it }
    override suspend fun stop() {
        session.value = null
    }
    override fun observeHistory(): Flow<List<SunscreenEvent>> = history
    override suspend fun record(event: SunscreenEvent) = history.update { it + event }
    override suspend fun syncFromRemote() = Unit
    override suspend fun clear() {
        session.value = null
        history.value = emptyList()
    }
}

class FakeUvHistoryRepository : UvHistoryRepository {
    val days = MutableStateFlow<Map<LocalDate, Double>>(emptyMap())
    override fun observeDailyMaxUv(): Flow<Map<LocalDate, Double>> = days
    override suspend fun record(date: LocalDate, maxUv: Double) = days.update { it + (date to maxUv) }
    override suspend fun clear() {
        days.value = emptyMap()
    }
}

class FakeReminderScheduler : ReminderScheduler {
    var scheduledAt: Instant? = null
    override fun schedule(at: Instant) {
        scheduledAt = at
    }
    override fun cancel() {
        scheduledAt = null
    }
}

class FakeAuthRepository(var signInResult: AuthResult<AuthUser> = AuthResult.Success(AuthUser("id-1", "ana@exemplo.pt"))) : AuthRepository {
    val user = MutableStateFlow<AuthUser?>(null)
    var signInCalls = 0

    override fun observeUser(): Flow<AuthUser?> = user
    override suspend fun signIn(email: String, password: String): AuthResult<AuthUser> {
        signInCalls++
        return signInResult.also { if (it is AuthResult.Success) user.value = it.data }
    }
    override suspend fun signUp(email: String, password: String, name: String, city: String): AuthResult<SignUpOutcome> =
        AuthResult.Success(SignUpOutcome.ConfirmEmail)
    override suspend fun sendPasswordReset(email: String): AuthResult<Unit> = AuthResult.Success(Unit)
    override suspend fun signOut() {
        user.value = null
    }
    override suspend fun deleteAccount(): AuthResult<Unit> = AuthResult.Success(Unit)
}
