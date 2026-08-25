package com.example.bmi.ui.setting

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmi.R
import com.example.bmi.databinding.ActivityLauSettingBinding
import java.util.Locale

class LauSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLauSettingBinding

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
        LanguageItem("ar", "العربية")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLauSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(item.code)
            )
            finish()
        }
        binding.languageRecyclerView.adapter = adapter
        binding.languageRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.back.setOnClickListener {
            finish()
        }
    }
}