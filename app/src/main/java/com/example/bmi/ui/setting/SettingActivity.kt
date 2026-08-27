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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.R
import com.example.bmi.databinding.ActivitySettingBinding
import com.example.bmi.ui.CustomPopup
import kotlinx.coroutines.launch
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
}