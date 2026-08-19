package com.example.bmi.ui.recent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.databinding.ItemRecentRecordBinding
import com.example.bmi.ui.result.category.BmiCategory
import com.example.bmi.ui.result.category.ChildBmiThreshold
import com.example.bmi.ui.result.category.femaleChildBmi
import com.example.bmi.ui.result.category.maleChildBmi

class RecentlistAdapter : ListAdapter<BmiRecord,RecentlistAdapter.RecentViewHolder>(DIFF_CALLBACK) {

    private var onItemClick : ((BmiRecord) -> Unit)? = null

    fun setOnItemClick(listener: (BmiRecord) -> Unit) {
        onItemClick = listener
    }//由外部设置点击事件

    class RecentViewHolder(
        val binding: ItemRecentRecordBinding
    ) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(record: BmiRecord,
                 onItemClick: (BmiRecord) -> Unit)
        {

            // BMI
            binding.bmiValue.text =
                String.format("%.1f", record.bmi)
            // 获取分类
            val category = getCategory(record)
            // 分类名称
            binding.typeName.text = category.displayName

            // 分类图标
            binding.typeCycle.setImageResource(
                category.iconRes
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
        private val times = listOf(
            "Morning",
            "Afternoon",
            "Evening",
            "Night"
        )
        fun getCategory(record: BmiRecord) : BmiCategory
        {
            if (record.isChild){
                val threshold = getChildThreshold(record)
                return getChildCategory(record.bmi.toFloat(), threshold!!)
            }else
            {
                return getAdultCategory(record.bmi.toFloat())
            }
        }

        fun getChildCategory(
            bmi: Float,
            threshold: ChildBmiThreshold
        ): BmiCategory {
            return when {
                bmi < threshold.underweight ->
                    BmiCategory.UNDERWEIGHT

                bmi < threshold.normal ->
                    BmiCategory.NORMAL

                bmi < threshold.overweight ->
                    BmiCategory.OVERWEIGHT

                else ->
                    BmiCategory.OBESE_CLASS_I
            }
        }

        fun getAdultCategory(
            bmi: Float,
        ): BmiCategory {
            return when {
                bmi < 16f ->
                    BmiCategory.VSU

                bmi < 17f ->
                    BmiCategory.SU

                bmi < 18.5f ->
                    BmiCategory.UNDERWEIGHT

                bmi < 25f ->
                    BmiCategory.NORMAL

                bmi < 30f ->
                    BmiCategory.OVERWEIGHT

                bmi < 35f ->
                    BmiCategory.OBESE_CLASS_I

                bmi < 40f ->
                    BmiCategory.OBESE_CLASS_II

                else ->
                    BmiCategory.OBESE_CLASS_III
            }
        }

        fun getChildThreshold(record: BmiRecord): ChildBmiThreshold? {

            val table = if (record.gender == "male") {
                maleChildBmi
            } else {
                femaleChildBmi
            }

            return table.firstOrNull {
                it.age == record.age
            }
        }
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