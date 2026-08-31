package com.example.bmi.ui.input

import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import com.example.bmi.R
import com.example.bmi.databinding.ActivityInputBinding
import com.example.bmi.ui.BaseActivity
import com.example.bmi.ui.CustomPopup

class InputActivity : BaseActivity(){
    private lateinit var binding: ActivityInputBinding

    override fun getInsets(insets: WindowInsetsCompat): Insets {
        val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        return Insets.of(
            systemBarInsets.left,
            0,
            systemBarInsets.right,
            systemBarInsets.bottom
        )
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {//使得点击输入框外，就失去焦点
        if (ev.action == MotionEvent.ACTION_DOWN) {
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
        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindowInsets(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, InputFragment())
                commit()
            }
        }

        val showDeleteToast = intent.getBooleanExtra(
            "show_delete_toast",
            false
        )

        if (showDeleteToast && !isFinishing && !isDestroyed) {
            binding.root.post {
                CustomPopup.show(
                    this,
                    binding.root,
                    getString(R.string.delete_successfully),
                    R.drawable.success_icon
                )
            }
            intent.removeExtra("show_delete_toast")
        }
    }

}