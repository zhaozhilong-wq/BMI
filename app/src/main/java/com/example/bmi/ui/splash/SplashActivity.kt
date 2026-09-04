package com.example.bmi.ui.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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
        setContent {
            SplashScreen(
                onAnimationEnd = {
                    goNext()
                }
            )
        }
    }



    private fun goNext() {
        lifecycleScope.launch {

            val isNewUser = viewModel.isNewUser()

            val targetActivity = if (isNewUser) {
                InputActivity::class.java
            } else {
                MainActivity::class.java
            }

            startActivity(
                Intent(
                    this@SplashActivity,
                    targetActivity
                ).apply {
                    putExtra("open_page", 1)
                }
            )

            finish()
        }
    }
}