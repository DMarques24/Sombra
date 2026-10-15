package com.dmm.presentation.common

// O seletor tem "50+": fica guardado como 60 e aparece sempre como "50+"
const val SPF_50_PLUS = 60

val SPF_OPTIONS = listOf(15, 30, 50, SPF_50_PLUS)

fun spfLabel(spf: Int): String = if (spf > 50) "50+" else spf.toString()
