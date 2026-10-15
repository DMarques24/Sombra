package com.dmm.data.di

import com.dmm.data.BuildConfig
import com.dmm.data.remote.supabase.SupabaseConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = createHttpClient(OkHttp.create())
    fun createHttpClient(engine: HttpClientEngine): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
        }
        // Só nas versões de desenvolvimento: a versão da Play Store não escreve pedidos no Logcat
        if (BuildConfig.DEBUG) install(Logging) {
            level = LogLevel.BODY
            // Os pedidos ao Supabase levam palavras-passe e tokens: nunca vão para o Logcat
            filter { request -> SupabaseConfig.url.isBlank() || !request.url.toString().startsWith(SupabaseConfig.url) }
        }
        expectSuccess = true
    }
}