package com.example.bmi.ui.recent

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.databinding.ItemRecentRecordBinding
import com.example.bmi.ui.result.category.BmiClassifier

class RecentlistAdapter : ListAdapter<BmiRecord,RecentlistAdapter.RecentViewHolder>(DIFF_CALLBACK) {

    private var onItemClick : ((BmiRecord) -> Unit)? = null

    fun setOnItemClick(listener: (BmiRecord) -> Unit) {
        onItemClick = listener
    }//由外部设置点击事件

    class RecentViewHolder(
        val binding: ItemRecentRecordBinding
    ) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(
            record: BmiRecord,
            onItemClick: (BmiRecord) -> Unit
        ) {
            val context = binding.root.context

            val times = listOf(
                context.getString(R.string.morning),
                context.getString(R.string.afternoon),
                context.getString(R.string.evening),
                context.getString(R.string.night)
            )

            // BMI
            binding.bmiValue.text =
                String.format("%.1f", record.bmi)

            // 获取分类
            val category =
                BmiClassifier.classify(record)

            // 分类名称
            binding.typeName.text =
                context.getString(category.displayName)

            // 分类图标
            // 所有分类共用一个圆形 Drawable
            binding.typeCycle.background =
                AppCompatResources.getDrawable(
                    binding.root.context,
                    R.drawable.bg_type_cycle
                )

            // 根据分类设置圆形颜色
            binding.typeCycle.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        binding.root.context,
                        category.colorRes
                    )
                )

            // 日期
            binding.time.text =
                "${months[record.month]} ${record.day}, ${record.year}\n" +
                        times[record.time]

            binding.root.setOnClickListener {
                onItemClick.invoke(record)
            }
        }

        private val months = listOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "June",
            "July",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentViewHolder {

        val binding = ItemRecentRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RecentViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecentViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position),onItemClick ?: {})
    }

    companion object {

        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<BmiRecord>() {

                override fun areItemsTheSame(
                    oldItem: BmiRecord,
                    newItem: BmiRecord
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: BmiRecord,
                    newItem: BmiRecord
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }



}