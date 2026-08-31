package com.example.bmi.ui.setting

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.databinding.ActivitySettingBinding
import com.example.bmi.ui.BaseActivity
import com.example.bmi.ui.CustomPopup
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class SettingActivity : BaseActivity() {

    private val viewModel : SettingViewModel by viewModel()

    private lateinit var binding: ActivitySettingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setupWindowInsets(binding.root)


        binding.back.setOnClickListener { finish() }

        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateChecked(isChecked)
        }

        binding.personalContainer.setOnClickListener {
            LogDialog().show(
                supportFragmentManager,
                "LogDialog"
            )
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
            SyncDialog().show(
                supportFragmentManager,
                "SyncDialog"
            )
        }
        binding.language.setOnClickListener {
            val intent = Intent(this, LauSettingActivity::class.java)
            startActivity(intent)
        }
        binding.feedback.setOnClickListener {
            resultLauncher.launch(
                Intent(this, FeedbackActivity::class.java)
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                viewModel.isChecked.collect { isChecked ->
                    binding.toggleButton.isChecked = isChecked
                }
            }
        }

        binding.ads.setOnClickListener {
            viewModel.insertDebugData(generateDebugRecords())
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
                        binding.root,
                        getString(R.string.toast_feedback_text),
                        R.drawable.success_icon
                    )
                }
            }
        }

    private fun generateDebugRecords(): List<BmiRecord> {

        val records = mutableListOf<BmiRecord>()

        val calendar = Calendar.getInstance().apply {
            set(
                2026,
                Calendar.JULY,
                15,
                23,
                0,
                0
            )
            set(Calendar.MILLISECOND, 0)
        }
        val weightValues = listOf(
            65.2, 65.6, 65.1, 65.8, 65.4,
            64.9, 65.5, 65.9, 65.3, 64.8,
            65.2, 65.7, 65.1, 65.9, 65.5,
            64.7, 65.3, 65.8, 65.0, 64.6,
            65.1, 65.6, 65.2, 64.8, 65.4,
            65.9, 65.3, 64.9, 65.6, 65.1,
            64.5, 65.0, 65.7, 65.2, 64.7,
            65.3, 65.9, 65.4, 64.8, 65.1,
            65.6, 65.0, 64.6, 65.2, 65.8,
            65.3, 64.9, 65.6
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

                    time = 0,

                    createdAt = calendar.timeInMillis
                )
            )

            calendar.add(
                Calendar.DAY_OF_YEAR,
                1
            )
        }

        return records
    }
}