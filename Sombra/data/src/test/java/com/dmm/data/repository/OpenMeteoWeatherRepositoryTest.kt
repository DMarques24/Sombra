package com.dmm.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.dmm.data.di.NetworkModule
import com.dmm.data.local.CachedForecast
import com.dmm.data.local.LocalStore
import com.dmm.data.remote.OpenMeteoApi
import com.dmm.domain.common.DataError
import com.dmm.domain.common.DomainResult
import com.dmm.domain.weather.model.Forecast
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

// Repositório com o MockEngine do Ktor (sem internet) e a cache guardada em SharedPreferences reais (Robolectric)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class OpenMeteoWeatherRepositoryTest {

    private val forecastJson = javaClass.classLoader!!.getResource("forecast_coimbra.json")!!.readText()
    private val store = LocalStore(ApplicationProvider.getApplicationContext<Context>())
    private val clock = MutableClock(Instant.parse("2026-07-20T12:00:00Z"))

    // Cada resposta da lista é usada num pedido, por ordem
    private fun repository(vararg responses: MockRequestHandleScope.() -> io.ktor.client.request.HttpResponseData): OpenMeteoWeatherRepository {
        var call = 0
        val engine = MockEngine { responses[call++].invoke(this) }
        return OpenMeteoWeatherRepository(OpenMeteoApi(NetworkModule.createHttpClient(engine, enableLogging = false)), store, clock)
    }

    private val ok: MockRequestHandleScope.() -> io.ktor.client.request.HttpResponseData = {
        respond(forecastJson, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
    }
    private val noNetwork: MockRequestHandleScope.() -> io.ktor.client.request.HttpResponseData = { throw IOException("sem rede") }

    @Test
    fun `sucesso devolve a previsão com a hora do pedido e o fuso do sítio`() = runTest {
        val result = repository(ok).getForecast(40.2, -8.4)

        val forecast = (result as DomainResult.Success).data
        assertFalse(forecast.isFromCache)
        assertEquals(clock.instant(), forecast.fetchedAt)
        assertEquals(ZoneOffset.ofHours(1), forecast.zone)      // Lisboa no verão (+01:00), vindo do utc_offset_seconds
    }

    @Test
    fun `sem rede e sem cache dá NoConnection`() = runTest {
        assertEquals(DomainResult.Failure(DataError.NoConnection), repository(noNetwork).getForecast(40.2, -8.4))
    }

    @Test
    fun `sem rede mas com cache recente do mesmo sítio devolve a cache marcada como offline`() = runTest {
        val repo = repository(ok, noNetwork)
        repo.getForecast(40.2, -8.4)
        val firstFetch = clock.instant()
        clock.advance(Duration.ofHours(3))

        val forecast = (repo.getForecast(40.21, -8.41) as DomainResult.Success<Forecast>).data

        assertTrue(forecast.isFromCache)
        assertEquals(firstFetch, forecast.fetchedAt)            // para o aviso "dados de há 3h"
    }

    @Test
    fun `a cache guarda a localização arredondada a 1 casa decimal`() = runTest {
        repository(ok).getForecast(40.20334, -8.41029)

        val cached = store.read("forecast_cache", CachedForecast.serializer())!!
        assertEquals(40.2, cached.latitude, 0.0)
        assertEquals(-8.4, cached.longitude, 0.0)
    }

    @Test
    fun `a cache não serve para outro sítio nem quando tem mais de 2 dias`() = runTest {
        val repo = repository(ok, noNetwork, noNetwork)
        repo.getForecast(40.2, -8.4)

        assertEquals(DomainResult.Failure(DataError.NoConnection), repo.getForecast(38.7, -9.1))   // Lisboa, longe de Coimbra
        clock.advance(Duration.ofDays(3))
        assertEquals(DomainResult.Failure(DataError.NoConnection), repo.getForecast(40.2, -8.4))
    }

    @Test
    fun `erro 500 dá Server e não usa a cache porque o problema não é a rede`() = runTest {
        val result = repository({ respondError(HttpStatusCode.InternalServerError) }).getForecast(40.2, -8.4)

        assertEquals(DomainResult.Failure(DataError.Server), result)
    }

    @Test
    fun `JSON inválido dá Unknown`() = runTest {
        val result = repository({
            respond("""{"isto":"não é uma previsão"}""", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }).getForecast(40.2, -8.4)

        assertEquals(DomainResult.Failure(DataError.Unknown), result)
    }
}

// Relógio que o teste pode avançar (o java.time.Clock.fixed não muda)
private class MutableClock(private var now: Instant) : Clock() {
    fun advance(duration: Duration) {
        now = now.plus(duration)
    }
    override fun instant(): Instant = now
    override fun getZone(): ZoneId = ZoneOffset.UTC
    override fun withZone(zone: ZoneId): Clock = this
}
