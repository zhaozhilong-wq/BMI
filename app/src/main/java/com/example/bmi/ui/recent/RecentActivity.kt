package com.example.bmi.ui.recent

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.databinding.ActivityRecentBinding
import com.example.bmi.ui.result.ResultActivity
import com.example.bmi.ui.result.ResultMode
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecentActivity : AppCompatActivity() {

    private val viewModel : RecentViewModel by viewModel()

    private lateinit var binding: ActivityRecentBinding
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
        binding = ActivityRecentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val adapter = RecentlistAdapter()
        binding.recentRecyclerView.adapter = adapter
        val space =
            (15 * resources.displayMetrics.density).toInt()

        binding.recentRecyclerView.addItemDecoration(
            ItemSpaceDecoration(space)
        )
        adapter.setOnItemClick { record ->
            startActivity(
                ResultActivity.newIntent(this, ResultMode.HISTORY, record.id)
            )
        }
        binding.recentRecyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.loadRecords()
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.records.collect { records ->
                    adapter.submitList(records)
                }
            }
        }
        binding.backButton.setOnClickListener {
            finish()
        }

    }
}