package com.example.bmi.ui.setting

import android.app.Activity
import android.app.framework.base.collectEffect
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.R
import com.example.bmi.ui.CustomPopup
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.jvm.java

class SettingActivity : AppCompatActivity() {

    private val viewModel : SettingViewModel by viewModel()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            viewModel.collectEffect{effect ->
                when (effect) {

                    SettingEffect.SyncSuccess -> {
                        CustomPopup.show(
                            this@SettingActivity,
                            window.decorView,
                            getString(
                                R.string.sync_success_toast
                            ),
                            R.drawable.success_icon
                        )
                    }

                    SettingEffect.FeedbackSuccess -> {
                        CustomPopup.show(
                            this@SettingActivity,
                            window.decorView,
                            getString(
                                R.string.toast_feedback_text
                            ),
                            R.drawable.success_icon
                        )
                    }
                }
            }

            SettingScreen(
                uiState = uiState,
                dispatch = viewModel::dispatch,
                onBack = {finish()},
                onFeedback = {
                    resultLauncher.launch(Intent(this@SettingActivity, FeedbackActivity::class.java))
                },
                onLanguage = { startActivity(Intent(this@SettingActivity, LauSettingActivity::class.java)) },
            )
        }
    }



    override fun onStop() {
        CustomPopup.dismiss()
        super.onStop()
    }

    private val resultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val saveSuccess =
                    result.data?.getBooleanExtra(
                        "save_success",
                        false
                    ) ?: false

                if (saveSuccess && !isFinishing && !isDestroyed) {
                    CustomPopup.show(
                        this,
                        window.decorView,
                        getString(R.string.toast_feedback_text),
                        R.drawable.success_icon
                    )
                }
            }
        }
}