package com.dmm.presentation.skintype

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmm.core.navigation.SkinTypeRoute

fun NavGraphBuilder.skinTypeScreen(onNext: () -> Unit) {
    composable<SkinTypeRoute> {
        SkinTypeScreen(onNext = onNext)
    }
}
