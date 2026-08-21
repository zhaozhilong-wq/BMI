package com.example.bmi.ui.main

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.bmi.R
import com.example.bmi.databinding.ActivityMainBinding
import com.example.bmi.ui.BmiDialConfig
import com.example.bmi.ui.BmiSection
import kotlinx.coroutines.launch
import com.example.bmi.ui.input.InputFragment
import com.example.bmi.ui.result.ResultFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel : MainViewModel by viewModel()



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
        //结果页点击任意地方，返回输入页
        if (
            ev.action == MotionEvent.ACTION_UP &&
            binding.viewPager.currentItem == 1
        ) {
            val recent = binding.root.findViewById<View>(R.id.recent)
            if (recent != null) {
                val location = IntArray(2)
                recent.getLocationOnScreen(location)
                val left = location[0]
                val top = location[1]
                val right = left + recent.width
                val bottom = top + recent.height
                val isInsideRecent =
                    ev.rawX >= left &&
                            ev.rawX <= right &&
                            ev.rawY >= top &&
                            ev.rawY <= bottom
                if (!isInsideRecent) {
                    goToInputPage()
                }
            } else {
                // 找不到 Recent，就直接跳转
                goToInputPage()
            }
        }


        return super.dispatchTouchEvent(ev)
    }


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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
        val viewPager2 = binding.viewPager
        val bottomNav = binding.bottomNav
        viewPager2.isUserInputEnabled = false


        viewPager2.adapter = Adapter(this)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_calculator -> viewPager2.currentItem = 0
                R.id.navigation_bmi -> viewPager2.currentItem = 1
                R.id.navigation_statistics -> viewPager2.currentItem = 2
            }
            true
        }//底部导航栏监听，用户点击底部导航栏，viewPage2跳转到对应页面：更新页面

        //监听页面更新导航栏
        viewPager2.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> bottomNav.selectedItemId = R.id.navigation_calculator

                        1 -> bottomNav.selectedItemId = R.id.navigation_bmi

                        2 -> bottomNav.selectedItemId = R.id.navigation_statistics
                    }
                }
            }
        )


        handleIntent(intent)






    }

    override fun onNewIntent(
        intent: Intent,
        caller: ComponentCaller
    ) {
        super.onNewIntent(intent, caller)

        setIntent(intent)
        handleIntent(intent)
    }



    fun goToInputPage() {
        binding.viewPager.currentItem = 0
    }

    private fun handleIntent(intent: Intent) {
        val openPage = intent.getIntExtra(
            "open_page",
            0
        )
        binding.viewPager.currentItem = openPage
        val showSavedToast = intent.getBooleanExtra(
            "show_saved_toast",
            false
        )
        if (showSavedToast) {
            Toast.makeText(
                this,
                "Saved successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}