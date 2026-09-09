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
                    // 修改 App Locale
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(
                            language.code
                        )
                    )
                }
            )
        }

    }
}