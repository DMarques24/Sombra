package com.dmm.presentation.forecast

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmm.core.navigation.ForecastRoute

fun NavGraphBuilder.forecastScreen(){
    composable<ForecastRoute> {
        ForecastScreen()
    }
}