package com.example.bmi.ui.recent

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.R
import com.example.bmi.ui.CustomPopup
import com.example.bmi.ui.result.ResultActivity
import com.example.bmi.ui.result.ResultMode
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecentActivity : AppCompatActivity() {

    private val viewModel : RecentViewModel by viewModel()

    override fun onStop() {
        CustomPopup.dismiss()
        super.onStop()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            RecentScreen(
                uiState = uiState,
                onBackClick = {
                    finish()
                },
                onItemClick = { recordId ->
                    resultLauncher.launch(
                        ResultActivity.newIntent(
                            this@RecentActivity,
                            ResultMode.HISTORY,
                            recordId
                        )
                    )
                }
            )
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

                if (deleteSuccess && !isFinishing && !isDestroyed) {
                    CustomPopup.show(
                        this,
                        window.decorView,
                        getString(R.string.delete_successfully),
                        R.drawable.success_icon
                    )
                }
            }
        }
}