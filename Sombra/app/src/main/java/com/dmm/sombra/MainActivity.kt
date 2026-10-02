package com.dmm.sombra

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.main.MainViewModel
import com.dmm.sombra.navigation.SombraNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Fica à espera de verificar se tem login feito ou não
        splash.setKeepOnScreenCondition { viewModel.isLoading.value }
        splash.setOnExitAnimationListener { splashView -> playSplashExitAnimation(splashView) }
        enableEdgeToEdge()
        setContent { SombraTheme { Surface { SombraNavHost() } } }
    }

    private fun playSplashExitAnimation(splashView: SplashScreenViewProvider) {
        val icon = splashView.iconView

        val pulse = ObjectAnimator.ofPropertyValuesHolder(
            icon,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.25f, 0.9f, 1.1f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.25f, 0.9f, 1.1f, 1f),
        ).apply {
            duration = 900L
            interpolator = AccelerateDecelerateInterpolator()
        }

        val fadeOut = ObjectAnimator.ofFloat(splashView.view, View.ALPHA, 1f, 0f).apply {
            duration = 300L
        }

        AnimatorSet().apply {
            playSequentially(pulse, fadeOut)
            doOnEnd { splashView.remove() }
            start()
        }
    }
}