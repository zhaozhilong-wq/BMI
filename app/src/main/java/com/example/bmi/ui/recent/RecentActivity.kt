package com.example.bmi.ui.recent

import android.app.Activity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmi.R
import com.example.bmi.databinding.ActivityRecentBinding
import com.example.bmi.ui.BaseActivity
import com.example.bmi.ui.CustomPopup
import com.example.bmi.ui.result.ResultActivity
import com.example.bmi.ui.result.ResultMode
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecentActivity : BaseActivity<ActivityRecentBinding>() {

    private val viewModel : RecentViewModel by viewModel()


    override fun createBinding(): ActivityRecentBinding {
        return ActivityRecentBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = RecentlistAdapter()
        binding.recentRecyclerView.adapter = adapter
        val space =
            (15 * resources.displayMetrics.density).toInt()

        binding.recentRecyclerView.addItemDecoration(
            ItemSpaceDecoration(space)
        )
        adapter.setOnItemClick { record ->
            resultLauncher.launch(
                ResultActivity.newIntent(
                    this,
                    ResultMode.HISTORY,
                    record.id
                )
            )
        }
        binding.recentRecyclerView.layoutManager = LinearLayoutManager(this)
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

    private val resultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val deleteSuccess =
                    result.data?.getBooleanExtra(
                        "delete_success",
                        false
                    ) ?: false

                if (deleteSuccess && !isFinishing && !isDestroyed) {
                    CustomPopup.show(
                        this,
                        binding.root,
                        getString(R.string.delete_successfully),
                        R.drawable.success_icon
                    )
                }
            }
        }
}