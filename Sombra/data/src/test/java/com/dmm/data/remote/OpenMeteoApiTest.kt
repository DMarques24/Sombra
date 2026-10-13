package com.dmm.data.remote

import com.dmm.data.di.NetworkModule
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Test

// Testes sem internet: o MockEngine faz de servidor e devolve um JSON real
// guardado em src/test/resources/forecast_coimbra.json
class OpenMeteoApiTest {

    private val forecastJson = javaClass.classLoader!!
        .getResource("forecast_coimbra.json")!!
        .readText()

    @Test
    fun `getForecast pede o URL e os parametros certos`() = runTest {
        val engine = MockEngine { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("api.open-meteo.com", request.url.host)
            assertEquals("/v1/forecast", request.url.encodedPath)
            val params = request.url.parameters
            assertEquals("40.2033", params["latitude"])
            assertEquals("-8.4103", params["longitude"])
            assertEquals("temperature_2m,apparent_temperature,is_day,weather_code,uv_index", params["current"])
            assertEquals("uv_index", params["hourly"])
            assertEquals("weather_code,temperature_2m_max,temperature_2m_min,uv_index_max,sunset", params["daily"])
            assertEquals("auto", params["timezone"])
            assertEquals("5", params["forecast_days"])
            respondJson(forecastJson)
        }

        OpenMeteoApi(NetworkModule.createHttpClient(engine)).getForecast(40.2033, -8.4103)

        assertEquals(1, engine.requestHistory.size)
    }

    @Test
    fun `getForecast converte a resposta nos DTOs`() = runTest {
        val api = OpenMeteoApi(NetworkModule.createHttpClient(MockEngine { respondJson(forecastJson) }))

        val forecast = api.getForecast(40.2033, -8.4103)

        assertEquals("Europe/Lisbon", forecast.timezone)
        assertEquals(20.9, forecast.current.temperature, 0.0)
        assertEquals(1, forecast.current.isDay)
        assertEquals(0, forecast.current.weatherCode)
        assertEquals(5 * 24, forecast.hourly.time.size)             // 5 dias de horas
        assertEquals(forecast.hourly.time.size, forecast.hourly.uvIndex.size)
        assertEquals(5, forecast.daily.time.size)
        assertEquals(5, forecast.daily.temperatureMax.size)
    }

    @Test(expected = ServerResponseException::class)
    fun `getForecast lanca excecao quando o servidor falha`() = runTest {
        // expectSuccess = true no NetworkModule: um 500 tem de dar erro, não um DTO vazio
        val api = OpenMeteoApi(
            NetworkModule.createHttpClient(MockEngine { respondError(HttpStatusCode.InternalServerError) })
        )

        api.getForecast(40.2033, -8.4103)
    }

    @Test
    fun `getForecast aceita uv_index em falta no current`() = runTest {
        // À noite a Open-Meteo pode não mandar uv_index; o DTO tem default null
        val root = Json.parseToJsonElement(forecastJson).jsonObject
        val current = JsonObject(root.getValue("current").jsonObject - "uv_index")
        val semUv = JsonObject(root + ("current" to current)).toString()
        val api = OpenMeteoApi(NetworkModule.createHttpClient(MockEngine { respondJson(semUv) }))

        val forecast = api.getForecast(40.2033, -8.4103)

        assertEquals(null, forecast.current.uvIndex)
    }

    private fun io.ktor.client.engine.mock.MockRequestHandleScope.respondJson(json: String) =
        respond(
            content = json,
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
}
