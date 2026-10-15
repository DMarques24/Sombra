package com.dmm.domain.progress.model

// A coleção de patches. As regras para desbloquear cada um estão em ObserveProgressUseCase.
enum class Patch {
    FirstSunscreen,     // aplicou protetor pela primeira vez
    SpfWeek,            // 7 dias seguidos com protetor
    Reapplier,          // 10 reaplicações a tempo
    ShadeHour,          // saiu do sol entre as 12h e as 16h
    HatOnTop,           // chegou ao nível Chapéu
    ExtremeUv,          // aplicou protetor com UV 11 ou mais
    ProtectedSummer,    // 30 dias protegido no verão
    Family,             // chegou ao nível Guarda-sol
}
