package com.dmm.data.di

import javax.inject.Qualifier

// Um CoroutineScope que vive tanto como a app: para trabalho que não pode ser cancelado
// quando um ecrã fecha (ex.: copiar o perfil para o Supabase)
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
