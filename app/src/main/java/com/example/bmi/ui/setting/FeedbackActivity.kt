package com.example.bmi.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.example.bmi.databinding.ActivityFeedbackBinding
import com.example.bmi.ui.BaseActivity

class FeedbackActivity : AppCompatActivity() {


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