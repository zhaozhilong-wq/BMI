package com.example.bmi.ui.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.PathInterpolator
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.animation.AnimatorListenerAdapter
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.lifecycle.lifecycleScope
import com.example.bmi.R
import com.example.bmi.databinding.ActivitySplashBinding
import com.example.bmi.ui.input.InputActivity
import com.example.bmi.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
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
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        binding.pointer.doOnLayout {
            binding.pointer.pivotX = binding.pointer.width / 2f
            binding.pointer.pivotY = binding.pointer.height * 19f / 23f
            binding.pointer.rotation = 0f
        }

        binding.dial.alpha = 0f
        binding.title.alpha = 0f

        binding.dial.translationY = dpToPx(20f)
        binding.title.translationY = dpToPx(20f)


        startSplashAnimation()
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    private fun createEnterAnimation(): AnimatorSet {

        val interpolator = PathInterpolator(
            0.25f,
            0f,
            0.1f,
            0.1f
        )

        val dialMove = ObjectAnimator.ofFloat(
            binding.dial,
            View.TRANSLATION_Y,
            dpToPx(100f),
            0f
        ).apply {
            duration = 1000L
            this.interpolator = interpolator
        }

        val titleMove = ObjectAnimator.ofFloat(
            binding.title,
            View.TRANSLATION_Y,
            dpToPx(100f),
            0f
        ).apply {
            duration = 1000L
            this.interpolator = interpolator
        }

        val dialAlpha = ObjectAnimator.ofFloat(
            binding.dial,
            View.ALPHA,
            0f,
            1f
        ).apply {
            duration = 1000L
        }

        val titleAlpha = ObjectAnimator.ofFloat(
            binding.title,
            View.ALPHA,
            0f,
            1f
        ).apply {
            duration = 1000L
        }
        return AnimatorSet().apply {
            playTogether(
                dialMove,
                titleMove,
                dialAlpha,
                titleAlpha
            )
        }
    }

    //创建指针旋转动画
    private fun createPointerToYellow(): ObjectAnimator {
        return ObjectAnimator.ofFloat(
            binding.pointer,
            View.ROTATION,
            0f,
            25f
        ).apply {
            duration = 1000L

            interpolator = PathInterpolator(
                0.25f,
                0f,
                0.1f,
                0.1f
            )
        }
    }
    private fun createPointerToGreen(): ObjectAnimator {
        return ObjectAnimator.ofFloat(
            binding.pointer,
            View.ROTATION,
            25f,
            -25f
        ).apply {
            duration = 1000L

            interpolator = PathInterpolator(
                0.25f,
                0f,
                0.1f,
                0.1f
            )
        }
    }

    private fun startSplashAnimation() {
        val firstStage = AnimatorSet().apply {
            playTogether(
                createEnterAnimation(),
                createPointerToYellow()
            )
        }

        val secondStage = createPointerToGreen()
        AnimatorSet().apply {
            playSequentially(
                firstStage,
                secondStage
            )
            addListener(
                object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {

                        lifecycleScope.launch {

                            // 2 秒动画结束后，再停留 1 秒
                            delay(1000L)

                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    InputActivity::class.java
                                )
                            )

                            finish()
                        }
                    }
                }
            )

            start()
        }
    }
}