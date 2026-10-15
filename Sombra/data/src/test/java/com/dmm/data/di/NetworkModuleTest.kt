package com.dmm.data.di

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.pluginOrNull
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

// Segurança: na versão de release os pedidos (com email, tokens e localização) não podem ir para o Logcat
class NetworkModuleTest {

    private val engine = MockEngine { respondOk() }

    @Test
    fun `sem logging (release) o plugin Logging não é instalado`() {
        val client = NetworkModule.createHttpClient(engine, enableLogging = false)

        assertNull(client.pluginOrNull(Logging))
    }

    @Test
    fun `com logging (debug) o plugin é instalado`() {
        val client = NetworkModule.createHttpClient(engine, enableLogging = true)

        assertNotNull(client.pluginOrNull(Logging))
    }
}
