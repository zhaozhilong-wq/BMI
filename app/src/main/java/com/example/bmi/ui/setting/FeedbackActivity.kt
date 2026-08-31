package com.example.bmi.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.example.bmi.R
import com.example.bmi.databinding.ActivityFeedbackBinding
import com.example.bmi.ui.BaseActivity

class FeedbackActivity : BaseActivity() {

    private lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            val ime = insets.getInsets(
                WindowInsetsCompat.Type.ime()
            )

            // 键盘高度
            val imeBottom = ime.bottom

            // 正常情况下使用导航栏高度
            // 键盘弹出后使用键盘高度
            val bottomInset = maxOf(
                systemBars.bottom,
                imeBottom
            )

            binding.inputText.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = bottomInset + dpToPx(20)
            }

            binding.root.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                0
            )

            insets
        }
        binding.back.setOnClickListener {
            finish()
        }
        binding.saveButton.setOnClickListener {
            setResult(
                Activity.RESULT_OK,
                Intent().apply {
                    putExtra("save_success", true)
                }
            )
            finish()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }


}