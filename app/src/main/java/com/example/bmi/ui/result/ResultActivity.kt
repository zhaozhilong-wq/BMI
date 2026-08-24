package com.example.bmi.ui.result

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bmi.R
import com.example.bmi.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
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
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {

            val mode = intent.getStringExtra(EXTRA_MODE)
                ?.let { ResultMode.valueOf(it) }
                ?: ResultMode.NEW_USER


            val fragment = ResultFragment.newInstance(
                mode = mode,
                recordId = intent.getLongExtra(EXTRA_RECORD_ID, 0)
            )

            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.resultContainer,
                    fragment
                )
                .commit()
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