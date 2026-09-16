package com.example.bmi.ui.splash

import android.app.framework.base.collectEffect
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.bmi.ui.BaseActivity
import com.example.bmi.ui.input.InputActivity
import com.example.bmi.ui.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity() {

    private val viewModel: SplashViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            viewModel.collectEffect { effect ->

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
                                putExtra("open_page", 1)
                            }
                        )
                        finish()
                    }
                }
            }
            SplashScreen(
                onAnimationEnd = {
                    viewModel.dispatch(
                        SplashEvent.AnimationEnd
                    )
                }
            )
        }
    }



}