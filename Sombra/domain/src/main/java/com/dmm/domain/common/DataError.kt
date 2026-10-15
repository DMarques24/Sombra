package com.dmm.domain.common

// Os erros que a app sabe tratar. O :domain não sabe nada de Ktor nem de HTTP:
// é o :data que traduz as exceções para isto (ver safeApiCall)
enum class DataError { NoConnection, Server, Unknown }
