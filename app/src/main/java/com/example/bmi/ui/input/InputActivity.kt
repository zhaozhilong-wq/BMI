package com.example.bmi.ui.input

import android.app.Activity
import android.app.framework.base.collectEffect
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.R
import com.example.bmi.ui.CustomPopup
import com.example.bmi.ui.result.ResultActivity
import com.example.bmi.ui.setting.SettingActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class InputActivity : AppCompatActivity(){

    private val viewModel : InputViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                viewModel.collectEffect { effect ->

                    when (effect) {

                        is InputEffect.ShowToast -> {
                            CustomPopup.show(
                                this@InputActivity,
                                window.decorView,
                                getString(
                                    effect.resId,
                                    effect.range
                                ),
                                R.drawable.warning_icon
                            )
                        }

                        is InputEffect.NavigateToResult -> {
                            resultLauncher.launch(
                                ResultActivity.newIntent(
                                    this@InputActivity,
                                    effect.mode,
                                    effect.recordId
                                )
                            )
                        }

                    }}
                InputScreen(
                    uiState = uiState,
                    dispatch = viewModel::dispatch,
                    onUserClick = {
                        startActivity(
                            Intent(
                                this@InputActivity,
                                SettingActivity::class.java
                            )
                        )
                    }
                )
            }
        }

        val showDeleteToast = intent.getBooleanExtra(
            "show_delete_toast",
            false
        )

        if (showDeleteToast && !isFinishing && !isDestroyed) {
            window.decorView.post {
                CustomPopup.show(
                    this,
                    window.decorView,
                    getString(R.string.delete_successfully),
                    R.drawable.success_icon
                )
            }
            intent.removeExtra("show_delete_toast")
        }

    }

    private val resultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->


            if (result.resultCode == Activity.RESULT_OK) {

                val deleteSuccess =
                    result.data?.getBooleanExtra(
                        "delete_success",
                        false
                    ) ?: false

                if (!deleteSuccess && !isFinishing && !isDestroyed) {
                    return@registerForActivityResult
                }

                if (deleteSuccess ) {
                    CustomPopup.show(
                        this@InputActivity,
                        window.decorView,
                        getString(R.string.delete_successfully),
                        R.drawable.success_icon
                    )
                }
            }
        }

}