package com.example.bmi.ui.input

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.bmi.R
import com.example.bmi.databinding.ActivityInputBinding

class InputActivity : AppCompatActivity() {
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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //输入页年龄选择滚动设置
        val ages = (2..99).toList()
        val layoutManager = LinearLayoutManager(
            this,
            RecyclerView.HORIZONTAL,
            false
        )
        binding.agePicker.layoutManager = layoutManager
        binding.agePicker.post {
            val itemWidth =
                binding.agePicker.width / 5
            // 左右留出半个 item 的空间
            val sidePadding =
                (binding.agePicker.width - itemWidth) / 2
            binding.agePicker.setPadding(
                sidePadding,
                0,
                sidePadding,
                0
            )
            val adapter = AgePickerAdapter(
                ages,
                itemWidth
            )
            binding.agePicker.adapter = adapter
            val snapHelper = OneItemSnapHelper()
            snapHelper.attachToRecyclerView(
                binding.agePicker
            )
            // 初始 25
            val initialPosition = 25 - 2
            layoutManager.scrollToPosition(
                initialPosition
            )
            fun updateSelectedItem(
                recyclerView: RecyclerView,
                layoutManager: LinearLayoutManager,
                snapHelper: SnapHelper
            ) {
                // 先把当前可见的全部变灰
                for (i in 0 until recyclerView.childCount) {
                    val child =
                        recyclerView.getChildAt(i)
                    child.findViewById<TextView>(
                        R.id.tvNumber
                    ).alpha = 0.3f
                }
                // 找到正中央的 item
                val snapView =
                    snapHelper.findSnapView(
                        layoutManager
                    ) ?: return
                // 只有中央这个变黑
                snapView.findViewById<TextView>(
                    R.id.tvNumber
                ).alpha = 1f
            }
            binding.agePicker.post {
                snapHelper.snapToCenter(
                    binding.agePicker
                )
                updateSelectedItem(
                    binding.agePicker,
                    layoutManager,
                    snapHelper
                )
            }
            binding.agePicker.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {

                    override fun onScrollStateChanged(
                        recyclerView: RecyclerView,
                        newState: Int
                    ) {
                        super.onScrollStateChanged(
                            recyclerView,
                            newState
                        )
                        if (
                            newState ==
                            RecyclerView.SCROLL_STATE_IDLE
                        ) {
                            updateSelectedItem(
                                recyclerView,
                                layoutManager,
                                snapHelper
                            )
                            val snapView =
                                snapHelper.findSnapView(
                                    layoutManager
                                ) ?: return
                            val position =
                                layoutManager.getPosition(
                                    snapView
                                )
                            val age = ages[position]
                            Log.d(
                                "AgePicker",
                                "当前年龄 = $age"
                            )
                        }
                    }
                }
            )
        }

        //绑定点击事件

    }

}