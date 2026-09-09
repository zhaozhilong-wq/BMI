package com.example.bmi.ui.splash

import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.ui.input.InputActivity
import com.example.bmi.ui.main.MainActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        observeEffect()

        setContent {
            SplashScreen(
                onAnimationEnd = {
                    viewModel.onIntent(
                        SplashIntent.AnimationEnd
                    )
                }
            )
        }
    }



    private fun observeEffect() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.effect.collect { effect ->

                    when (effect) {

                        SplashEffect.OpenInput -> {
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    InputActivity::class.java
                                )
                            )

                            finish()
                        }

                        SplashEffect.OpenMain -> {
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    MainActivity::class.java
                                ).apply {
                                    putExtra(
                                        "open_page",
                                        1
                                    )
                                }
                            )

                            finish()
                        }
                    }
                }
            }
        }
    }
}