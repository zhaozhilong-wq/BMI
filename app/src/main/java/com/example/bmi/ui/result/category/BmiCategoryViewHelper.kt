package com.example.bmi.ui.result.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.example.bmi.R
import com.example.bmi.databinding.ItemBmiTypeBinding

object BmiCategoryViewHelper {

    fun createAdultCategoryItems(): List<BmiCategoryItem> {
        return listOf(
            BmiCategoryItem(
                category = BmiCategory.VSU,
                name = "Very Severely Underweight",
                range = "<16.0",
                cycleRes = R.drawable.vsu_cycle,
                backgroundRes = R.drawable.result_vsu,
            ),
            BmiCategoryItem(
                category = BmiCategory.SU,
                name = "Severely Underweight",
                range = "16.0-16.9",
                cycleRes = R.drawable.su_cycle,
                backgroundRes = R.drawable.result_su
            ),
            BmiCategoryItem(
                category = BmiCategory.UNDERWEIGHT,
                name = "Underweight",
                range = "17.0-18.4",
                cycleRes = R.drawable.u_cycle,
                backgroundRes = R.drawable.result_underweight
            ),
            BmiCategoryItem(
                category = BmiCategory.NORMAL,
                name = "Normal",
                range = "18.5-24.9",
                cycleRes = R.drawable.normal_cycle,
                backgroundRes = R.drawable.result_normal
            ),
            BmiCategoryItem(
                category = BmiCategory.OVERWEIGHT,
                name = "Overweight",
                range = "25.0-29.9",
                cycleRes = R.drawable.overweight_cycle,
                backgroundRes = R.drawable.result_overweight
            ),
            BmiCategoryItem(
                category = BmiCategory.OBESE_CLASS_I,
                name = "Obese Class I",
                range = "30.0-34.9",
                cycleRes = R.drawable.oc1_cycle,
                backgroundRes = R.drawable.result_oc1
            ),
            BmiCategoryItem(
                category = BmiCategory.OBESE_CLASS_II,
                name = "Obese Class II",
                range = "35.0-39.9",
                cycleRes = R.drawable.oc2_cycle,
                backgroundRes = R.drawable.result_oc2
            ),
            BmiCategoryItem(
                category = BmiCategory.OBESE_CLASS_III,
                name = "Obese Class III",
                range = "≥40.0",
                cycleRes = R.drawable.oc3_cycle,
                backgroundRes = R.drawable.result_oc3
            )
        )
    }

    fun createChildCategoryItems(
        threshold: ChildBmiThreshold
    ): List<BmiCategoryItem> {
        return listOf(

            BmiCategoryItem(
                category = BmiCategory.UNDERWEIGHT,
                name = "Underweight",
                range = "<${formatBmi(threshold.underweight)}",
                cycleRes = R.drawable.u_cycle,
                backgroundRes = R.drawable.result_underweight
            ),

            BmiCategoryItem(
                category = BmiCategory.NORMAL,
                name = "Normal",
                range = "${formatBmi(threshold.underweight)}-${formatBmi(threshold.normal)}",
                cycleRes = R.drawable.normal_cycle,
                backgroundRes = R.drawable.result_normal
            ),

            BmiCategoryItem(
                category = BmiCategory.OVERWEIGHT,
                name = "Overweight",
                range = "${formatBmi(threshold.normal)}-${formatBmi(threshold.overweight)}",
                cycleRes = R.drawable.overweight_cycle,
                backgroundRes = R.drawable.result_overweight
            ),

            BmiCategoryItem(
                category = BmiCategory.OBESE_CLASS_I,
                name = "Obese",
                range = "≥${formatBmi(threshold.overweight)}",
                cycleRes = R.drawable.oc1_cycle,
                backgroundRes = R.drawable.result_oc1
            )
        )

    }

    private fun formatBmi(value: Float): String {
        return String.format("%.1f", value)
    }

    fun setup(
        container: ViewGroup,
        items: List<BmiCategoryItem>,
        selectedCategory: BmiCategory,
        inflater: LayoutInflater
    ) {
        container.removeAllViews()

        items.forEach { item ->

            val itemBinding = ItemBmiTypeBinding.inflate(
                inflater,
                container,
                false
            )

            itemBinding.typeText.text = inflater.context.getString(item.category.displayName)
            itemBinding.typeRange.text = item.range
            itemBinding.typeCycle.setImageResource(item.cycleRes)

            if (item.category == selectedCategory) {
                itemBinding.root.setBackgroundResource(
                    item.backgroundRes
                )
                itemBinding.typeCycle.setImageResource(R.drawable.cycle_selected)
                itemBinding.typeText.setTextColor(
                    itemBinding.typeText.context.getColor(
                        R.color.white
                    )
                )
                itemBinding.typeText.typeface =
                    ResourcesCompat.getFont(itemBinding.typeText.context, R.font.montserrat_extrabold)
                itemBinding.typeText.alpha = 1f
                itemBinding.typeRange.setTextColor(
                    itemBinding.typeRange.context.getColor(
                        R.color.white
                    )
                )
                itemBinding.typeRange.typeface =
                    ResourcesCompat.getFont(itemBinding.typeRange.context, R.font.montserrat_extrabold)
                itemBinding.typeRange.alpha = 1f
            }

            container.addView(itemBinding.root)
        }
    }
}