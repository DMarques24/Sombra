package com.dmm.domain.progress.model

// Os níveis, do mais fácil ao mais difícil. minXp = XP preciso para lá chegar.
enum class Level(val number: Int, val minXp: Int) {
    Sunglasses(1, 0),
    Cap(2, 200),
    Hat(3, 500),
    Parasol(4, 1000),
    Shade(5, 2000);

    val next: Level? get() = entries.getOrNull(ordinal + 1)

    companion object {
        fun fromXp(xp: Int): Level = entries.last { xp >= it.minXp }
    }
}
