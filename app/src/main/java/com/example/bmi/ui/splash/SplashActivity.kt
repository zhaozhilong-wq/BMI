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

        binding.pointer.doOnLayout {//等这个View完成layout后，再执行里面的代码
            binding.pointer.pivotX = binding.pointer.width / 2f
            binding.pointer.pivotY = binding.pointer.height * 19f / 23f
            binding.pointer.rotation = 0f
        }//设置指针旋转中心x,y坐标

        binding.dial.alpha = 0f
        binding.title.alpha = 0f//初始化透明度

        binding.dial.translationY = dpToPx(20f)
        binding.title.translationY = dpToPx(20f)//初始化位移，


        startSplashAnimation()
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    private fun createEnterAnimation(): AnimatorSet {//返回的是动画集合

        val interpolator = PathInterpolator(
            0.25f,
            0f,
            0.1f,
            0.1f
        )//设置动画速度

        val dialMove = ObjectAnimator.ofFloat(
            binding.dial,
            View.TRANSLATION_Y,//设置位移
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
            View.ALPHA,//透明度从0-1
            0f,
            1f
        ).apply {
            duration = 1000L
        }

        val titleAlpha = ObjectAnimator.ofFloat(
            binding.title,
            View.ALPHA,
            0f,//透明度从0-1
            1f
        ).apply {
            duration = 1000L
        }
        return AnimatorSet().apply {
            playTogether(//同时开始
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
            View.ROTATION,//从0-25度旋转
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
            25f,//从25到-25旋转
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
        }//第一阶段动画

        val secondStage = createPointerToGreen()
        AnimatorSet().apply {
            playSequentially(
                firstStage,
                secondStage
            )//第二阶段动画，sequentially是上个结束下个才开始
            addListener(//动画监听器,监听结束后要干什么
                object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {

                        lifecycleScope.launch {

                            // 2 秒动画结束后，再停留 1 秒
                            delay(1000L)

                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    MainActivity::class.java
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