package com.example.bmi.ui.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.PathInterpolator
import android.animation.AnimatorListenerAdapter
import androidx.activity.compose.setContent
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.lifecycle.lifecycleScope
import com.example.bmi.databinding.ActivitySplashBinding
import com.example.bmi.ui.BaseActivity
import com.example.bmi.ui.input.InputActivity
import com.example.bmi.ui.main.MainActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun createBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    private val viewModel: SplashViewModel by viewModel()


    override fun getInsets(insets: WindowInsetsCompat): Insets {
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        return Insets.of(systemBars.left, systemBars.top, systemBars.right, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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