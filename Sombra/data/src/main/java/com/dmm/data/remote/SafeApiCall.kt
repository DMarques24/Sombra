package com.dmm.data.remote

import com.dmm.domain.common.DataError
import com.dmm.domain.common.DomainResult
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.CancellationException
import java.io.IOException

/**
 * Corre uma chamada à rede e converte qualquer exceção num DataError.
 * Tudo o que está no :data passa por aqui, e assim nenhuma exceção chega à UI.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): DomainResult<T> = try {
    DomainResult.Success(block())
} catch (e: CancellationException) {
    throw e                                        // cancelamento normal: deixa passar
} catch (e: IOException) {
    DomainResult.Failure(DataError.NoConnection)   // sem rede, timeout, DNS
} catch (e: ResponseException) {
    DomainResult.Failure(DataError.Server)         // a API respondeu 4xx ou 5xx
} catch (e: JsonConvertException) {
    DomainResult.Failure(DataError.Unknown)        // a resposta veio num formato inesperado
} catch (e: Exception) {
    DomainResult.Failure(DataError.Unknown)        // qualquer outra coisa
}