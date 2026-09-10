package com.example.bmi.ui.result

import android.app.Activity
import android.app.framework.base.collectEffect
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bmi.ui.input.InputActivity
import com.example.bmi.ui.main.MainActivity
import com.example.bmi.ui.recent.RecentActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResultActivity : AppCompatActivity() {

    private val viewModel: ResultViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {

            val mode = intent.getStringExtra(EXTRA_MODE)
                ?.let { ResultMode.valueOf(it) }
                ?: ResultMode.NEW_USER

            val recordId = intent.getLongExtra(EXTRA_RECORD_ID, 0)

            viewModel.dispatch(
                ResultEvent.LoadRecord(
                    mode = mode,
                    recordId = recordId
                )
            )

            setContent {
                val uiState by
                viewModel.uiState.collectAsStateWithLifecycle()

                viewModel.collectEffect { effect ->

                    when (effect) {

                        ResultEffect.DeleteAndGoToInput -> {

                            startActivity(
                                Intent(
                                    this,
                                    InputActivity::class.java
                                ).apply {
                                    putExtra(
                                        "show_delete_toast",
                                        true
                                    )
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            )

                            finish()
                        }

                        ResultEffect.DeleteAndFinish -> {

                            setResult(
                                Activity.RESULT_OK,
                                Intent().apply {
                                    putExtra(
                                        "delete_success",
                                        true
                                    )
                                }
                            )

                            finish()
                        }
                        else -> {}
                    }
                }
                ResultScreen(
                    uiState = uiState,
                    mode = mode,
                    dispatch = viewModel::dispatch,
                    onBack = {finish()},
                    onRecent = {
                        startActivity(Intent(this, RecentActivity::class.java))
                    },
                    onBackgroundClick = {
                        (this as MainActivity).goToInputPage()
                    },
                    onSave = {
                        startActivity(
                            Intent(
                                this,
                                MainActivity::class.java
                            ).apply {
                                putExtra("open_page", 2)
                                putExtra("show_saved_toast", true)
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            }
                        )

                        finish()
                    }
                )
            }
        }
    }
    companion object {

        private const val EXTRA_MODE = "extra_mode"
        private const val EXTRA_RECORD_ID = "extra_record_id"

        fun newIntent(
            context: Context,
            mode: ResultMode,
            recordId: Long
        ): Intent {

            return Intent(context, ResultActivity::class.java).apply {

                putExtra(EXTRA_MODE, mode.name)
                putExtra(EXTRA_RECORD_ID, recordId)
            }
        }
    }
}