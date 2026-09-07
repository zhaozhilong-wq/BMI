package com.example.bmi.ui.setting

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.example.bmi.ui.input.InputActivity
import com.example.bmi.ui.main.MainActivity
import com.example.bmi.ui.splash.SplashViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class LauSettingActivity : AppCompatActivity() {

    private val viewModel : SplashViewModel by viewModel()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentLocale = AppCompatDelegate
            .getApplicationLocales()
            .get(0)

        val currentLanguageCode =
            currentLocale?.toLanguageTag()
                ?: Locale.getDefault().toLanguageTag()

        val selectedLanguageCode =
            languages
                .firstOrNull { language ->
                    language.code.equals(
                        currentLanguageCode,
                        ignoreCase = true
                    )
                }
                ?.code
                ?: "en"

        setContent {

            LanguageSettingScreen(
                selectedLanguageCode = selectedLanguageCode,

                onBackClick = {
                    finish()
                },

                onLanguageClick = { language ->

                    lifecycleScope.launch {

                        // 判断是不是新用户
                        val isNewUser = viewModel.isNewUser()

                        // 根据用户状态决定进入哪个页面
                        val targetActivity =
                            if (isNewUser) {
                                InputActivity::class.java
                            } else {
                                MainActivity::class.java
                            }

                        // 修改 App Locale
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(
                                language.code
                            )
                        )

                        // 清掉当前 Activity 栈
                        val intent = Intent(
                            this@LauSettingActivity,
                            targetActivity
                        ).apply {
                            addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            )
                        }

                        startActivity(intent)

                        // 不播放 Activity 切换动画
                        overridePendingTransition(0, 0)
                    }
                }
            )
        }

    }
}