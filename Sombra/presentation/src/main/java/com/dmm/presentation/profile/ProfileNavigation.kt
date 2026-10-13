package com.dmm.presentation.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmm.core.navigation.ProfileRoute

fun NavGraphBuilder.profileScreen() {
    composable<ProfileRoute> {
        ProfileScreen()
    }
}
