package com.example.bmi.ui.input

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bmi.R
import com.example.bmi.databinding.ActivityInputBinding

class InputActivity : AppCompatActivity(){
    private lateinit var binding: ActivityInputBinding

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
        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, InputFragment())
            commit()
        }

        val showDeleteToast = intent.getBooleanExtra(
            "show_delete_toast",
            false
        )

        if (showDeleteToast) {
            Toast.makeText(
                this,
                "Deleted successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}