package com.example.bmi.ui.setting

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.R
import com.example.bmi.databinding.ActivitySettingBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingActivity : AppCompatActivity() {

    private val viewModel : SettingViewModel by viewModel()

    private lateinit var binding: ActivitySettingBinding
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
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.back.setOnClickListener { finish() }

        binding.toggleButton.setOnClickListener {
            if (viewModel.isChecked) {
                // 右 → 左
                binding.toggleCircle.animate()
                    .translationX(0f)
                    .setDuration(200)
                    .start()
                binding.rail.backgroundTintList =
                    ColorStateList.valueOf(
                        getColor(R.color.rail_un_checked)
                    )
                binding.toggleCircle.backgroundTintList =
                    ColorStateList.valueOf(
                        getColor(R.color.white)
                    )
                viewModel.check()
            } else {
                // 左 → 右
                binding.toggleCircle.animate()
                    .translationX(
                        dpToPx(14f)
                    )
                    .setDuration(200)
                    .start()
                binding.rail.backgroundTintList =
                    ColorStateList.valueOf(
                        getColor(R.color.rail_checked)
                    )
                binding.toggleCircle.backgroundTintList =
                    ColorStateList.valueOf(
                        getColor(R.color.rail_checked)
                    )
                viewModel.check()
            }
        }

        binding.personalContainer.setOnClickListener {
            val dialog = LogDialog(this, viewModel)
            dialog.show()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLogin.collect { isLogin ->
                    if (isLogin) {
                        binding.userImg.visibility = View.VISIBLE
                        binding.policy.visibility = View.GONE
                        binding.divide3.visibility = View.GONE
                        binding.name.text = "Cassie"
                        binding.email.text = "cassiexiao@gmail.com"
                    }else {
                        binding.userImg.visibility = View.GONE
                        binding.policy.visibility = View.VISIBLE
                        binding.divide3.visibility = View.VISIBLE
                        binding.name.text = "Backup & Restore"
                        binding.email.text = "Synchronize your data"
                    }
                }
            }
        }
        binding.synButton.setOnClickListener {

        }


    }
    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}