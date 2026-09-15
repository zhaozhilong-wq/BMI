package com.example.bmi.ui.main

import android.app.Activity
import android.app.framework.base.collectEffect
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bmi.R
import com.example.bmi.ui.BaseActivity
import com.example.bmi.ui.CustomPopup
import com.example.bmi.ui.input.InputActivity
import com.example.bmi.ui.input.InputEffect
import com.example.bmi.ui.input.InputViewModel
import com.example.bmi.ui.recent.RecentActivity
import com.example.bmi.ui.result.ResultActivity
import com.example.bmi.ui.result.ResultEffect
import com.example.bmi.ui.result.ResultEvent
import com.example.bmi.ui.result.ResultMode
import com.example.bmi.ui.result.ResultViewModel
import com.example.bmi.ui.setting.SettingActivity
import com.example.bmi.ui.statistics.StatisticsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val inputViewModel: InputViewModel by viewModel()
    private val resultViewModel: ResultViewModel by viewModel()
    private val statisticsViewModel: StatisticsViewModel by viewModel()

    private val mainViewModel: MainViewModel by viewModel()

    private val currentPage = MutableStateFlow(1)

    private var downX = 0f
    private var downY = 0f



    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {//使得点击输入框外，就失去焦点
        if (ev.action == MotionEvent.ACTION_DOWN) {
            // 记录手指按下的位置
            downX = ev.rawX
            downY = ev.rawY
            val currentFocus = currentFocus
            if (currentFocus is EditText) {
                val location = IntArray(2)
                currentFocus.getLocationOnScreen(location)
                val left = location[0]
                val top = location[1]
                val right = left + currentFocus.width
                val bottom = top + currentFocus.height
                val x = ev.rawX
                val y = ev.rawY
                // 点击的位置不在当前 EditText 内
                if (x < left || x > right || y < top || y > bottom) {
                    currentFocus.clearFocus()
                }
            }
        }


        return super.dispatchTouchEvent(ev)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )//防止键盘弹出时，布局被顶起

        val initialPage = intent.getIntExtra(
            "open_page",
            0
        )

        currentPage.value = initialPage

        resultViewModel.dispatch(
            ResultEvent.LoadRecord(
                mode = ResultMode.LATEST,
                recordId = -1L
            )
        )

        setContent {
            val inputUiState =
                inputViewModel.uiState.collectAsStateWithLifecycle()

            val resultUiState =
                resultViewModel.uiState.collectAsStateWithLifecycle()

            val statisticsUiState =
                statisticsViewModel.uiState.collectAsStateWithLifecycle()

            val page by currentPage.collectAsStateWithLifecycle()

            mainViewModel.collectEffect { effect ->

                when (effect) {

                    MainEffect.ShowSaveSuccess -> {
                        CustomPopup.show(
                            this@MainActivity,
                            window.decorView,
                            getString(R.string.save_successfully),
                            R.drawable.success_icon
                        )
                    }
                }
            }


            inputViewModel.collectEffect {effect ->

                when (effect) {

                    is InputEffect.ShowToast -> {
                        CustomPopup.show(
                            this@MainActivity,
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
                                this@MainActivity,
                                effect.mode,
                                effect.recordId
                            )
                        )
                    }

                } }

            resultViewModel.collectEffect { effect ->

                when (effect) {

                    ResultEffect.DeleteAndGoToInput -> {
                        startActivity(
                            Intent(
                                this@MainActivity,
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


            MainScreen(
                currentPage = page,
                onPageChanged = { page ->
                    currentPage.value = page
                },
                inputUiState = inputUiState.value,
                inputDispatch = inputViewModel::dispatch,
                onUserClick = {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            SettingActivity::class.java
                        )
                    )
                },
                resultUiState = resultUiState.value,
                mode = ResultMode.LATEST,
                resultDispatch = resultViewModel::dispatch,
                onBack = {finish()},
                onRecent = {
                    startActivity(Intent(this, RecentActivity::class.java))
                },
                onBackgroundClick = {
                   goToInputPage()
                },
                onSave = {
                },

                statisticsUiState = statisticsUiState.value,
                statisticsDispatch = statisticsViewModel::dispatch,
                onUpdate = {
                    goToInputPage()
                }
            )


            window.decorView.post {
                handleIntent(intent)
            }
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    fun goToInputPage() {
        currentPage.value = 0
    }





    private fun handleIntent(intent: Intent) {

        val openPage = intent.getIntExtra(
            "open_page",
            -1
        )

        if (openPage in 0..2) {
            currentPage.value = openPage
        }

        val showSavedToast = intent.getBooleanExtra(
            "show_saved_toast",
            false
        )
        if (showSavedToast) {
            mainViewModel.dispatch(
                MainEvent.SaveSuccess
            )
        }
        intent.removeExtra("show_saved_toast")
        intent.removeExtra("open_page")
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


                if (deleteSuccess ) {
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