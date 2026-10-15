package com.dmm.domain.common

// Resultado de uma operação que pode falhar: ou traz os dados, ou traz o erro.
// Assim o erro faz parte do tipo e o compilador obriga a tratá-lo
sealed interface DomainResult<out T> {
    data class Success<out T>(val data: T) : DomainResult<T>
    data class Failure(val error: DataError) : DomainResult<Nothing>
}

// Transforma os dados se correu bem; se falhou, o erro passa tal como está
inline fun <T, R> DomainResult<T>.map(transform: (T) -> R): DomainResult<R> = when (this) {
    is DomainResult.Success -> DomainResult.Success(transform(data))
    is DomainResult.Failure -> this
}
