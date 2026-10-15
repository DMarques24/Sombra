package com.dmm.domain.profile.model

// Fototipos de Fitzpatrick. med = dose mínima de UV (J/m²) que faz a pele ficar vermelha:
// quanto mais alta, mais tempo a pele aguenta ao sol sem protetor.
enum class SkinType(val med: Int) {
    I(200),
    II(250),
    III(300),
    IV(450),
    V(600),
    VI(1000),
}
