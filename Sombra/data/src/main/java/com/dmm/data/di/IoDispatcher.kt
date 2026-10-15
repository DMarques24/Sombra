package com.dmm.data.di

import javax.inject.Qualifier

// O dispatcher para código que bloqueia a thread (ex.: o Geocoder antigo). É injetado,
// e não escrito à mão, para os testes poderem trocá-lo por um TestDispatcher.
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher
