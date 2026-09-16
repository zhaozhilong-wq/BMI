package com.example.bmi.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.bmi.ui.BaseActivity


class FeedbackActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FeedbackScreen(
                onBackClick = {
                    finish()
                },
                onSaveClick = {
                    setResult(
                        Activity.RESULT_OK,
                        Intent().apply {
                            putExtra("save_success", true)
                        }
                    )
                    finish()
                }
            )
        }
    }
}