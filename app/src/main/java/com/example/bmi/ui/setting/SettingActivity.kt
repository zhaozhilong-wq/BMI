package com.example.bmi.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.ui.CustomPopup
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class SettingActivity : AppCompatActivity() {

    private val viewModel : SettingViewModel by viewModel()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isLogin by viewModel.isLogin.collectAsStateWithLifecycle()
            val isChecked by viewModel.isChecked.collectAsStateWithLifecycle()

            SettingScreen(
                isLogin = isLogin,
                isChecked = isChecked,

                onBackClick = {
                    finish()
                },


                onLanguageClick = {
                    startActivity(
                        Intent(
                            this@SettingActivity,
                            LauSettingActivity::class.java
                        )
                    )
                },

                onFeedbackClick = {
                    resultLauncher.launch(
                        Intent(
                            this@SettingActivity,
                            FeedbackActivity::class.java
                        )
                    )
                },

                onAdsClick = {
                    viewModel.insertDebugData(
                        generateDebugRecords()
                    )
                },

                onCheckedChange = { checked ->
                    viewModel.updateChecked(checked)
                },
                onSyncDone = {
                    CustomPopup.show(
                        this,
                        window.decorView,
                        getString(R.string.sync_success_toast),
                        R.drawable.success_icon
                    )
                },
                onLoginClick = {
                    viewModel.login()
                },
                onLogoutClick = {
                    viewModel.logout()
                },
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

    private fun generateDebugRecords(): List<BmiRecord> {

        val records = mutableListOf<BmiRecord>()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val weightValues = listOf(
            65.6, 66.6, 68.1, 67.8, 70.4,
            74.9, 75.5, 75.9, 74.3, 74.8,
            75.2, 72.7, 75.1, 72.9, 70.5,
            68.7, 67.3, 69.8, 68.0, 74.6,
            75.1, 72.6, 70.2, 74.8, 75.4,
            65.9, 65.3, 64.9, 65.6, 65.1,
            68.5, 69.0, 73.7, 75.2, 74.7,
            75.3, 72.9, 71.4, 69.8, 68.1,
            65.6, 63.0, 62.6, 64.2, 65.8,
            65.3, 66.9, 68.6
        )

        repeat(48) { index ->

            val weight = weightValues[index]
            val bmi = weight / (1.75 * 1.75)

            records.add(
                BmiRecord(
                    weightKg = weight,
                    heightCm = 175.0,

                    weightUnit = "kg",
                    heightUnit = "cm",

                    bmi = bmi,

                    age = 25,
                    gender = "male",
                    isChild = false,

                    year = calendar.get(Calendar.YEAR),
                    month = calendar.get(Calendar.MONTH),
                    day = calendar.get(Calendar.DAY_OF_MONTH),

                    time = 2,

                    createdAt = calendar.timeInMillis
                )
            )

            // 往前一天
            calendar.add(
                Calendar.DAY_OF_YEAR,
                -1
            )
        }

        return records
    }
}