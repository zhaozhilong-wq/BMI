package com.example.bmi.ui.main

import android.app.ComponentCaller
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.bmi.R
import com.example.bmi.databinding.ActivityInputBinding
import com.example.bmi.databinding.ActivityMainBinding
import com.example.bmi.ui.BaseActivity
import com.example.bmi.ui.CustomPopup
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun createBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

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
        //结果页点击任意地方，返回输入页
        if (
            ev.action == MotionEvent.ACTION_UP &&
            binding.viewPager.currentItem == 1
        ) {
            val dx = ev.rawX - downX
            val dy = ev.rawY - downY

            val distance = kotlin.math.sqrt(
                dx * dx + dy * dy
            )
            val touchSlop = ViewConfiguration.get(this).scaledTouchSlop
            if(distance < touchSlop)
            {

                // 判断是不是点击了 BottomNavigation
                val navLocation = IntArray(2)
                binding.bottomNav.getLocationOnScreen(navLocation)

                val navLeft = navLocation[0]
                val navTop = navLocation[1]
                val navRight = navLeft + binding.bottomNav.width
                val navBottom = navTop + binding.bottomNav.height

                val isInsideBottomNav =
                    ev.rawX >= navLeft &&
                            ev.rawX <= navRight &&
                            ev.rawY >= navTop &&
                            ev.rawY <= navBottom

                if (isInsideBottomNav) {
                    return super.dispatchTouchEvent(ev)
                }

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
        }


        return super.dispatchTouchEvent(ev)
    }

    override fun getInsets(insets: WindowInsetsCompat): Insets {
        val systemBars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars()
        )
        return Insets.of(
            systemBars.left,
            0,
            systemBars.right,
            0
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )
        val viewPager2 = binding.viewPager
        val bottomNav = binding.bottomNav
        viewPager2.isUserInputEnabled = false

        viewPager2.adapter = Adapter(this)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_calculator ->
                    viewPager2.setCurrentItem(0, false)

                R.id.navigation_bmi ->
                    viewPager2.setCurrentItem(1, false)

                R.id.navigation_statistics ->
                    viewPager2.setCurrentItem(2, false)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }



    fun goToInputPage() {
        if (binding.viewPager.currentItem != 0) {
            binding.viewPager.setCurrentItem(0, false)
        }
    }


    private fun handleIntent(intent: Intent) {

        val openPage = intent.getIntExtra(
            "open_page",
            -1
        )

        if (openPage in 0..2) {
            binding.viewPager.setCurrentItem(
                openPage,
                false
            )

            binding.bottomNav.selectedItemId = when (openPage) {
                0 -> R.id.navigation_calculator
                1 -> R.id.navigation_bmi
                2 -> R.id.navigation_statistics
                else -> return
            }
        }

        val showSavedToast = intent.getBooleanExtra(
            "show_saved_toast",
            false
        )
        if (showSavedToast && !isFinishing && !isDestroyed) {
            binding.root.post {
                    CustomPopup.show(
                        this,
                        binding.root,
                        getString(R.string.save_successfully),
                        R.drawable.success_icon
                    )

            }
        }
        intent.removeExtra("show_saved_toast")
        intent.removeExtra("open_page")
    }


}