package com.example.bmi.ui.setting

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.Insets
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmi.R
import com.example.bmi.databinding.ActivityLauSettingBinding
import com.example.bmi.ui.BaseActivity
import com.example.bmi.ui.input.InputActivity
import com.example.bmi.ui.main.MainActivity
import com.example.bmi.ui.splash.SplashViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class LauSettingActivity : BaseActivity<ActivityLauSettingBinding>() {
    override fun createBinding(): ActivityLauSettingBinding {
        return ActivityLauSettingBinding.inflate(layoutInflater)
    }

    private val viewModel : SplashViewModel by viewModel()

    val languages = listOf(
        LanguageItem("en", "English"),
        LanguageItem("pt", "Português"),
        LanguageItem("ru", "Русский"),
        LanguageItem("de", "Deutsch"),
        LanguageItem("zh-TW", "繁體中文"),
        LanguageItem("zh-CN", "简体中文"),
        LanguageItem("fr", "Français"),
        LanguageItem("es", "Español"),
        LanguageItem("it", "Italiano"),
        LanguageItem("ko", "한국어"),
        LanguageItem("ar", "العربية"),
        LanguageItem("fa", "فارسی"),
        LanguageItem("id", "Bahasa Indonesia"),
        LanguageItem("ja", "日本語"),
        LanguageItem("nl", "Nederlands"),
        LanguageItem("pl", "Polski"),
        LanguageItem("th", "ไทย"),
        LanguageItem("tr", "Türkçe"),
        LanguageItem("vi", "Tiếng Việt"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentLocale = AppCompatDelegate
            .getApplicationLocales()
            .get(0)

        val currentLanguageCode =
            currentLocale?.toLanguageTag()
                ?: Locale.getDefault().toLanguageTag()

        val selectedPosition = languages.indexOfFirst {
            it.code.equals(currentLanguageCode, ignoreCase = true)
        }.let {
            if (it == -1) 0 else it
        }
        val adapter = LanguageAdapter(
            languages = languages,
            selectedPosition = selectedPosition
        ) { item ->
            lifecycleScope.launch{
                val isNewUser = viewModel.isNewUser()
                val targetActivity = if (isNewUser) {
                    InputActivity::class.java
                } else {
                    MainActivity::class.java
                }
                val intent = Intent(this@LauSettingActivity, targetActivity)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                overridePendingTransition(0,0)
            }

            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(item.code)
            )
        }
        binding.languageRecyclerView.adapter = adapter
        binding.languageRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.back.setOnClickListener {
            finish()
        }
    }
}