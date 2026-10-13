package com.dmm.data.remote

import com.dmm.data.di.NetworkModule
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test

// Chama a API verdadeira (precisa de internet). Fica com @Ignore para não falhar
// sem rede; tira o @Ignore (ou corre-o no Android Studio) quando quiseres confirmar
// que a Open-Meteo ainda responde no formato que os DTOs esperam.
@Ignore("Usa a internet: correr à mão")
class OpenMeteoApiLiveTest {

    @Test
    fun `a Open-Meteo responde para Coimbra`() = runTest {
        val api = OpenMeteoApi(NetworkModule.createHttpClient(OkHttp.create()))

        val forecast = api.getForecast(latitude = 40.2033, longitude = -8.4103)

        println(forecast)
        assertEquals("Europe/Lisbon", forecast.timezone)
        assertEquals(5, forecast.daily.time.size)
        assertTrue(forecast.current.temperature in -30.0..55.0)
    }
}
